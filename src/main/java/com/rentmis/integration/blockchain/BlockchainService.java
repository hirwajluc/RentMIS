package com.rentmis.integration.blockchain;

import com.rentmis.dto.response.ContractVerifyResponse;
import com.rentmis.model.entity.Contract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

/**
 * Contract integrity service using HMAC-SHA256 + SHA-256 hashing —
 * HMAC-SHA256 based blockchain integrity verification.
 *
 * No real blockchain RPC calls are made; a deterministic 32-char hex
 * "blockchain reference" is computed locally and stored as the tx hash.
 *
 * Flow:
 *   1. contractHash  = SHA256(canonical contract fields)
 *   2. landlordSig   = HMAC-SHA256("landlord|{hash}|owner:{id}|ts:{ts}", signingSecret)
 *   3. tenantSig     = HMAC-SHA256("tenant|{hash}|landlord_sig:{lSig}|tenant:{id}|ts:{ts}", signingSecret)
 *   4. blockchainRef = SHA256("{hash}|{lSig}|{tSig}")[:32]
 */
@Slf4j
@Service
public class BlockchainService {

    @Value("${blockchain.signing-secret:${jwt.secret:RentMIS-contract-signing-secret-v1}}|rentmis-contracts-v1")
    private String signingSecret;

    // ─── Public API ────────────────────────────────────────────────────────────

    /** Compute SHA-256 contract hash from canonical field string. */
    public String generateContractHash(Contract contract) {
        String data = buildContractData(contract);
        return sha256(data);
    }

    /**
     * After landlord signs: compute landlord HMAC signature and store it.
     * Returns the signature (caller must persist it on the contract).
     */
    public String computeLandlordSignature(Contract contract, long timestampEpoch) {
        String input = "landlord|" + contract.getContractHash()
                + "|owner:" + contract.getLandlord().getId()
                + "|ts:" + timestampEpoch;
        return hmacSha256(input, signingSecret);
    }

    /**
     * After tenant signs: compute tenant HMAC signature and store it.
     * Returns the signature (caller must persist it on the contract).
     */
    public String computeTenantSignature(Contract contract, long timestampEpoch) {
        String landlordSig = contract.getLandlordSignature() != null ? contract.getLandlordSignature() : "";
        String input = "tenant|" + contract.getContractHash()
                + "|landlord_sig:" + landlordSig
                + "|tenant:" + contract.getTenant().getId()
                + "|ts:" + timestampEpoch;
        return hmacSha256(input, signingSecret);
    }

    /**
     * Once both parties have signed: compute the final "blockchain reference"
     * (SHA-256 of hash + both signatures, first 32 hex chars).
     */
    public BlockchainRecordResult computeBlockchainRef(Contract contract) {
        if (contract.getContractHash() == null
                || contract.getLandlordSignature() == null
                || contract.getTenantSignature() == null) {
            log.warn("Cannot compute blockchain ref for {} — missing signatures", contract.getContractNumber());
            return BlockchainRecordResult.builder()
                    .success(false)
                    .errorMessage("Missing one or more signatures")
                    .build();
        }

        String combined = contract.getContractHash()
                + "|" + contract.getLandlordSignature()
                + "|" + contract.getTenantSignature();
        String ref = sha256(combined).substring(0, 32);

        return BlockchainRecordResult.builder()
                .txHash("0x" + ref)
                .network("RentMIS-CryptoRef-v1")
                .contractHash(contract.getContractHash())
                .timestamp(LocalDateTime.now())
                .success(true)
                .build();
    }

    /**
     * Verifies a contract's integrity by recomputing its hash and
     * re-deriving the blockchain ref. Returns a simple boolean.
     */
    public boolean verifyContract(Contract contract) {
        return verifyContractFull(contract).isValid();
    }

    /**
     * Full verification — recomputes hash + blockchain ref and returns
     * a detailed ContractVerifyResponse describing every check.
     */
    public ContractVerifyResponse verifyContractFull(Contract contract) {
        String tenantName   = contract.getTenant().getFirstName() + " " + contract.getTenant().getLastName();
        String landlordName = contract.getLandlord().getFirstName() + " " + contract.getLandlord().getLastName();
        String property     = contract.getUnit().getProperty() != null
                ? contract.getUnit().getProperty().getName() : "";

        // 1 — Hash check
        if (contract.getContractHash() == null) {
            return ContractVerifyResponse.builder()
                    .status("PENDING").valid(false)
                    .contractNumber(contract.getContractNumber())
                    .contractStatus(contract.getStatus().name())
                    .message("Contract hash not yet computed.")
                    .landlordSigned(contract.getLandlordSignedAt() != null)
                    .tenantSigned(contract.getTenantSignedAt() != null)
                    .landlordSignedAt(contract.getLandlordSignedAt())
                    .tenantSignedAt(contract.getTenantSignedAt())
                    .tenantName(tenantName).landlordName(landlordName)
                    .propertyName(property).unitNumber(contract.getUnit().getUnitNumber())
                    .startDate(contract.getStartDate()).endDate(contract.getEndDate())
                    .monthlyRent("RWF " + contract.getMonthlyRent().toPlainString())
                    .build();
        }

        String recomputedHash = generateContractHash(contract);
        boolean hashMatch     = recomputedHash.equals(contract.getContractHash());

        if (!hashMatch) {
            log.warn("Contract {} hash MISMATCH — possible tampering", contract.getContractNumber());
            return ContractVerifyResponse.builder()
                    .status("TAMPERED").valid(false)
                    .contractNumber(contract.getContractNumber())
                    .contractStatus(contract.getStatus().name())
                    .message("⚠ Contract data has been modified after signing. This document may not be genuine.")
                    .storedHash(contract.getContractHash())
                    .recomputedHash(recomputedHash).hashMatch(false)
                    .blockchainRef(contract.getBlockchainTxHash())
                    .landlordSigned(contract.getLandlordSignedAt() != null)
                    .tenantSigned(contract.getTenantSignedAt() != null)
                    .landlordSignedAt(contract.getLandlordSignedAt())
                    .tenantSignedAt(contract.getTenantSignedAt())
                    .tenantName(tenantName).landlordName(landlordName)
                    .propertyName(property).unitNumber(contract.getUnit().getUnitNumber())
                    .startDate(contract.getStartDate()).endDate(contract.getEndDate())
                    .monthlyRent("RWF " + contract.getMonthlyRent().toPlainString())
                    .build();
        }

        // 2 — Blockchain ref check (only when both parties have signed)
        boolean bothSigned      = contract.getLandlordSignedAt() != null && contract.getTenantSignedAt() != null;
        boolean blockchainMatch = false;
        String  recomputedRef   = null;

        if (bothSigned && contract.getBlockchainTxHash() != null) {
            BlockchainRecordResult ref = computeBlockchainRef(contract);
            if (ref.isSuccess()) {
                recomputedRef   = ref.getTxHash();
                blockchainMatch = contract.getBlockchainTxHash().equals(recomputedRef);
                if (!blockchainMatch) {
                    log.warn("Contract {} blockchain ref MISMATCH", contract.getContractNumber());
                }
            }
        }

        String status, message;
        boolean valid;
        if (!bothSigned) {
            status  = "PARTIAL";
            valid   = true; // hash is intact, just not fully signed yet
            message = "Contract hash is intact. Awaiting " +
                      (contract.getLandlordSignedAt() == null ? "landlord" : "tenant") + " signature.";
        } else if (!blockchainMatch && contract.getBlockchainTxHash() != null) {
            status  = "TAMPERED";
            valid   = false;
            message = "⚠ Blockchain reference mismatch — signature data may have been altered.";
        } else {
            status  = "VALID";
            valid   = true;
            message = "✔ Contract is genuine. Hash and blockchain reference both verified.";
        }

        return ContractVerifyResponse.builder()
                .status(status).valid(valid).message(message)
                .contractNumber(contract.getContractNumber())
                .contractStatus(contract.getStatus().name())
                .storedHash(contract.getContractHash())
                .recomputedHash(recomputedHash).hashMatch(true)
                .blockchainRef(contract.getBlockchainTxHash())
                .recomputedBlockchainRef(recomputedRef)
                .blockchainRefMatch(blockchainMatch)
                .landlordSigned(contract.getLandlordSignedAt() != null)
                .tenantSigned(contract.getTenantSignedAt() != null)
                .landlordSignedAt(contract.getLandlordSignedAt())
                .tenantSignedAt(contract.getTenantSignedAt())
                .tenantName(tenantName).landlordName(landlordName)
                .propertyName(property).unitNumber(contract.getUnit().getUnitNumber())
                .startDate(contract.getStartDate()).endDate(contract.getEndDate())
                .monthlyRent("RWF " + contract.getMonthlyRent().toPlainString())
                .build();
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private String buildContractData(Contract contract) {
        return String.join("|",
                "RentMIS",
                contract.getContractNumber(),
                "TENANT:"  + contract.getTenant().getEmail(),
                "LANDLORD:"+ contract.getLandlord().getEmail(),
                "UNIT:"    + contract.getUnit().getUnitNumber(),
                "PROPERTY:"+ (contract.getUnit().getProperty() != null
                        ? contract.getUnit().getProperty().getName() : ""),
                "RENT:"    + contract.getMonthlyRent().toPlainString(),
                "DEPOSIT:" + (contract.getDepositAmount() != null
                        ? contract.getDepositAmount().toPlainString() : "0"),
                "START:"   + contract.getStartDate().toString(),
                "END:"     + contract.getEndDate().toString(),
                "TERMS:"   + (contract.getTermsConditions() != null
                        ? contract.getTermsConditions() : "")
        );
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 unavailable", e);
        }
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("HmacSHA256 unavailable", e);
        }
    }
}
