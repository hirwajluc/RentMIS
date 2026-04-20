package com.rentmis.service.impl;

import com.rentmis.audit.AuditService;
import com.rentmis.dto.request.ContractRequest;
import com.rentmis.dto.response.ContractResponse;
import com.rentmis.dto.response.ContractVerifyResponse;
import com.rentmis.exception.RentMISException;
import com.rentmis.integration.blockchain.BlockchainRecordResult;
import com.rentmis.integration.blockchain.BlockchainService;
import com.rentmis.mapper.ContractMapper;
import com.rentmis.model.entity.Contract;
import com.rentmis.model.entity.Property;
import com.rentmis.model.entity.Unit;
import com.rentmis.model.entity.User;
import com.rentmis.model.enums.ContractStatus;
import com.rentmis.model.enums.UnitStatus;
import com.rentmis.repository.ContractRepository;
import com.rentmis.repository.UnitRepository;
import com.rentmis.repository.UserRepository;
import com.rentmis.service.ContractPdfService;
import com.rentmis.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl {

    private final ContractRepository contractRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final BlockchainService blockchainService;
    private final ContractMapper contractMapper;
    private final ContractPdfService contractPdfService;
    private final AuditService auditService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final AgentServiceImpl agentService;

    @Transactional
    public ContractResponse createContract(ContractRequest request, Long landlordId, String ip) {
        User tenant = userRepository.findById(request.getTenantId())
                .orElseThrow(() -> RentMISException.notFound("Tenant not found"));

        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> RentMISException.notFound("Unit not found"));

        User landlord = userRepository.findById(landlordId)
                .orElseThrow(() -> RentMISException.notFound("Landlord not found"));

        // Validate landlord owns this unit
        if (!unit.getProperty().getLandlord().getId().equals(landlordId)) {
            throw RentMISException.forbidden("You do not own this property");
        }

        if (unit.getStatus() == UnitStatus.OCCUPIED) {
            throw RentMISException.conflict("Unit is already occupied");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw RentMISException.badRequest("End date must be after start date");
        }

        String contractNumber = ReferenceGenerator.generateContractNumber();

        Contract contract = Contract.builder()
                .contractNumber(contractNumber)
                .tenant(tenant)
                .landlord(landlord)
                .unit(unit)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .monthlyRent(request.getMonthlyRent())
                .depositAmount(request.getDepositAmount())
                .termsConditions(request.getTermsConditions())
                .specialClauses(request.getSpecialClauses())
                .status(ContractStatus.PENDING_SIGNATURE)
                .build();

        contract = contractRepository.save(contract);

        // Generate contract hash and record on blockchain async
        recordOnBlockchain(contract);

        auditService.log("CONTRACT_CREATED", "Contract", contract.getId(), ip);
        return contractMapper.toResponse(contract);
    }

    @Async
    @Transactional
    public void recordOnBlockchain(Contract contract) {
        try {
            Contract fresh = contractRepository.findById(contract.getId())
                    .orElse(contract);
            if (fresh.getContractHash() != null) return; // already hashed

            String hash = blockchainService.generateContractHash(fresh);
            fresh.setContractHash(hash);
            fresh.setBlockchainNetwork("RentMIS-CryptoRef-v1");
            fresh.setBlockchainTimestamp(java.time.LocalDateTime.now());
            contractRepository.save(fresh);
            log.info("Contract {} hash computed: {}", fresh.getContractNumber(), hash);
        } catch (Exception e) {
            log.error("Contract hashing failed for {}: {}", contract.getContractNumber(), e.getMessage());
        }
    }

    @Transactional
    public ContractResponse signContract(Long contractId, String signerType, String ip,
                                         Long signerId, String password) {
        // Verify password before allowing signature
        User signer = userRepository.findById(signerId)
                .orElseThrow(() -> RentMISException.notFound("User not found"));
        if (!passwordEncoder.matches(password, signer.getPassword())) {
            throw RentMISException.badRequest("Incorrect password");
        }

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> RentMISException.notFound("Contract not found"));

        // Only allow signing when contract is awaiting signatures or requires re-signing
        if (contract.getStatus() == ContractStatus.TERMINATED
                || contract.getStatus() == ContractStatus.EXPIRED) {
            throw RentMISException.badRequest("Cannot sign a " + contract.getStatus().name().toLowerCase() + " contract");
        }

        long ts = System.currentTimeMillis() / 1000;

        // Ensure hash exists before signing
        if (contract.getContractHash() == null) {
            contract.setContractHash(blockchainService.generateContractHash(contract));
        }

        if ("LANDLORD".equals(signerType)) {
            contract.setLandlordSignedAt(LocalDateTime.now());
            contract.setLandlordSignatureIp(ip);
            contract.setLandlordSignature(blockchainService.computeLandlordSignature(contract, ts));
        } else {
            contract.setTenantSignedAt(LocalDateTime.now());
            contract.setTenantSignatureIp(ip);
            contract.setTenantSignature(blockchainService.computeTenantSignature(contract, ts));
        }

        // Both signed → activate and compute final blockchain ref
        if (contract.getLandlordSignedAt() != null && contract.getTenantSignedAt() != null) {
            contract.setStatus(ContractStatus.ACTIVE);

            BlockchainRecordResult ref = blockchainService.computeBlockchainRef(contract);
            if (ref.isSuccess()) {
                contract.setBlockchainTxHash(ref.getTxHash());
                contract.setBlockchainTimestamp(ref.getTimestamp());
                log.info("Contract {} blockchain ref: {}", contract.getContractNumber(), ref.getTxHash());
            }

            Unit unit = contract.getUnit();
            unit.setStatus(UnitStatus.OCCUPIED);
            unit.setCurrentTenant(contract.getTenant());
            unitRepository.save(unit);

            // Auto-trigger commission for any agent linkages on this unit
            try {
                Property prop = unit.getProperty();
                String unitRef = "Unit " + unit.getUnitNumber()
                        + (unit.getFloorNumber() != null ? ", Floor " + unit.getFloorNumber() : "");
                agentService.onContractSigned(
                        prop.getId(), unit.getId(),
                        prop.getName(), prop.getDistrict(), prop.getProvince(),
                        unitRef);
            } catch (Exception e) {
                log.error("Commission auto-trigger failed for contract {}: {}",
                        contract.getContractNumber(), e.getMessage());
            }
        }

        String auditAction = contract.getTamperDetectedAt() != null
                ? "CONTRACT_RESIGNED_" + signerType
                : "CONTRACT_SIGNED_" + signerType;
        contract = contractRepository.save(contract);
        auditService.log(auditAction, "Contract", contractId, ip);
        return contractMapper.toResponse(contract);
    }

    @Transactional
    public ContractResponse terminateContract(Long contractId, String reason, String ip) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> RentMISException.notFound("Contract not found"));

        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw RentMISException.badRequest("Only active contracts can be terminated");
        }

        contract.setStatus(ContractStatus.TERMINATED);
        contract.setTerminatedAt(LocalDateTime.now());
        contract.setTerminationReason(reason);

        Unit unit = contract.getUnit();
        unit.setStatus(UnitStatus.AVAILABLE);
        unit.setCurrentTenant(null);
        unitRepository.save(unit);

        contract = contractRepository.save(contract);
        auditService.log("CONTRACT_TERMINATED", "Contract", contractId, ip);
        return contractMapper.toResponse(contract);
    }

    @Transactional
    public ContractResponse verifyContractIntegrity(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> RentMISException.notFound("Contract not found"));
        enforceIfTampered(contract, "system");
        return contractMapper.toResponse(contract);
    }

    @Transactional
    public ContractVerifyResponse verifyByContractNumber(String contractNumber) {
        Contract contract = contractRepository.findByContractNumber(contractNumber)
                .orElseThrow(() -> RentMISException.notFound("Contract not found"));
        ContractVerifyResponse result = blockchainService.verifyContractFull(contract);
        if ("TAMPERED".equals(result.getStatus())) {
            enforceIfTampered(contract, "public-verify");
            result = ContractVerifyResponse.builder()
                    .status("TAMPERED")
                    .valid(false)
                    .message("⚠ Tampering detected. All signatures have been invalidated. " +
                             "Both parties must review and re-sign the contract before it is valid again.")
                    .contractNumber(contract.getContractNumber())
                    .contractStatus(contract.getStatus().name())
                    .storedHash(contract.getContractHash())
                    .recomputedHash(result.getRecomputedHash())
                    .hashMatch(result.isHashMatch())
                    .landlordSigned(false)
                    .tenantSigned(false)
                    .tenantName(result.getTenantName())
                    .landlordName(result.getLandlordName())
                    .propertyName(result.getPropertyName())
                    .unitNumber(result.getUnitNumber())
                    .startDate(result.getStartDate())
                    .endDate(result.getEndDate())
                    .monthlyRent(result.getMonthlyRent())
                    .build();
        }
        return result;
    }

    @Transactional
    public ContractVerifyResponse verifyByBlockchainRef(String blockchainRef) {
        Contract contract = contractRepository.findByBlockchainTxHash(blockchainRef)
                .orElseThrow(() -> RentMISException.notFound("No contract found with this blockchain reference"));
        ContractVerifyResponse result = blockchainService.verifyContractFull(contract);
        if ("TAMPERED".equals(result.getStatus())) {
            enforceIfTampered(contract, "public-verify");
            result = ContractVerifyResponse.builder()
                    .status("TAMPERED")
                    .valid(false)
                    .message("⚠ Tampering detected. All signatures have been invalidated. " +
                             "Both parties must review and re-sign the contract before it is valid again.")
                    .contractNumber(contract.getContractNumber())
                    .contractStatus(contract.getStatus().name())
                    .storedHash(contract.getContractHash())
                    .recomputedHash(result.getRecomputedHash())
                    .hashMatch(result.isHashMatch())
                    .landlordSigned(false)
                    .tenantSigned(false)
                    .tenantName(result.getTenantName())
                    .landlordName(result.getLandlordName())
                    .propertyName(result.getPropertyName())
                    .unitNumber(result.getUnitNumber())
                    .startDate(result.getStartDate())
                    .endDate(result.getEndDate())
                    .monthlyRent(result.getMonthlyRent())
                    .build();
        }
        return result;
    }

    /**
     * When tampering is detected: invalidate all signatures, reset hash to current data,
     * set status to PENDING_RESIGN so both parties must review and re-sign.
     */
    private void enforceIfTampered(Contract contract, String triggerIp) {
        // Skip if already enforced or in a terminal state
        if (contract.getStatus() == ContractStatus.PENDING_RESIGN
                || contract.getStatus() == ContractStatus.TERMINATED
                || contract.getStatus() == ContractStatus.EXPIRED) {
            return;
        }
        if (contract.getContractHash() == null) return;

        String recomputed = blockchainService.generateContractHash(contract);
        if (recomputed.equals(contract.getContractHash())) return; // no tampering

        log.warn("TAMPER ENFORCEMENT: Contract {} flagged — invalidating signatures, requiring re-sign",
                contract.getContractNumber());

        // Reset hash to current data so re-signing works correctly
        contract.setContractHash(recomputed);
        // Invalidate all signatures and blockchain proof
        contract.setLandlordSignature(null);
        contract.setTenantSignature(null);
        contract.setLandlordSignedAt(null);
        contract.setTenantSignedAt(null);
        contract.setLandlordSignatureIp(null);
        contract.setTenantSignatureIp(null);
        contract.setBlockchainTxHash(null);
        contract.setBlockchainTimestamp(null);
        // Flag for re-signing
        contract.setStatus(ContractStatus.PENDING_RESIGN);
        contract.setTamperDetectedAt(LocalDateTime.now());

        contractRepository.save(contract);
        auditService.log("CONTRACT_TAMPER_DETECTED", "Contract", contract.getId(), triggerIp);
    }

    @Transactional(readOnly = true)
    public Page<ContractResponse> getContracts(Long tenantId, Long landlordId,
                                                ContractStatus status, Pageable pageable) {
        return contractRepository.filterContracts(tenantId, landlordId, status, pageable)
                .map(contractMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ContractResponse getContract(Long id) {
        return contractMapper.toResponse(
                contractRepository.findById(id)
                        .orElseThrow(() -> RentMISException.notFound("Contract not found")));
    }

    @Transactional(readOnly = true)
    public byte[] generateContractPdf(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> RentMISException.notFound("Contract not found"));
        return contractPdfService.generate(contract);
    }
}
