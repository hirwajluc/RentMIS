package com.rentmis.service.impl;

import com.rentmis.audit.AuditService;
import com.rentmis.dto.request.LinkageRequest;
import com.rentmis.dto.response.*;
import com.rentmis.exception.RentMISException;
import com.rentmis.model.entity.*;
import com.rentmis.model.enums.CommissionStatus;
import com.rentmis.model.enums.LinkageStatus;
import com.rentmis.model.enums.UnitStatus;
import com.rentmis.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentServiceImpl {

    private final PropertyRepository    propertyRepository;
    private final UnitRepository        unitRepository;
    private final UserRepository        userRepository;
    private final LinkageRepository     linkageRepository;
    private final CommissionRepository  commissionRepository;
    private final AuditService          auditService;

    // ── Agent dashboard stats ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AgentDashboardResponse getDashboard(Long agentId) {
        return AgentDashboardResponse.builder()
                .totalLinkages(          linkageRepository.countByAgentId(agentId))
                .pendingLinkages(        linkageRepository.countByAgentIdAndStatus(agentId, LinkageStatus.PENDING))
                .acceptedLinkages(       linkageRepository.countByAgentIdAndStatus(agentId, LinkageStatus.ACCEPTED))
                .rejectedLinkages(       linkageRepository.countByAgentIdAndStatus(agentId, LinkageStatus.REJECTED))
                .contractSignedLinkages( linkageRepository.countByAgentIdAndStatus(agentId, LinkageStatus.CONTRACT_SIGNED))
                .expiredLinkages(        linkageRepository.countByAgentIdAndStatus(agentId, LinkageStatus.EXPIRED))
                .totalCommissions(       commissionRepository.countByAgentId(agentId))
                .pendingCommissions(     commissionRepository.countByAgentIdAndStatus(agentId, CommissionStatus.PENDING))
                .approvedCommissions(    commissionRepository.countByAgentIdAndStatus(agentId, CommissionStatus.APPROVED))
                .paidCommissions(        commissionRepository.countByAgentIdAndStatus(agentId, CommissionStatus.PAID))
                .build();
    }

    // ── Vacant property browse (privacy-safe) ────────────────────────────────

    @Transactional(readOnly = true)
    public Page<AgentPropertyResponse> getVacantProperties(String search, Pageable pageable) {
        return propertyRepository.findVacantProperties(search, pageable)
                .map(this::toAgentPropertyResponse);
    }

    @Transactional(readOnly = true)
    public AgentPropertyResponse getVacantProperty(Long propertyId) {
        Property p = propertyRepository.findById(propertyId)
                .orElseThrow(() -> RentMISException.notFound("Property not found"));

        boolean hasVacant = p.getUnits().stream()
                .anyMatch(u -> Boolean.TRUE.equals(u.getIsActive()) && u.getStatus() == UnitStatus.AVAILABLE);
        if (!hasVacant) {
            throw RentMISException.notFound("No vacant units available for this property");
        }
        return toAgentPropertyResponse(p);
    }

    // ── Linkage management ───────────────────────────────────────────────────

    @Transactional
    public LinkageResponse createLinkage(LinkageRequest request, Long agentId) {
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> RentMISException.notFound("Agent not found"));

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> RentMISException.notFound("Property not found"));

        if (!Boolean.TRUE.equals(property.getIsActive())) {
            throw RentMISException.badRequest("Property is not active");
        }

        Unit unit = null;
        if (request.getUnitId() != null) {
            unit = unitRepository.findById(request.getUnitId())
                    .filter(u -> u.getProperty().getId().equals(property.getId()))
                    .orElseThrow(() -> RentMISException.notFound("Unit not found on this property"));

            if (unit.getStatus() != UnitStatus.AVAILABLE) {
                throw RentMISException.conflict("Unit is not available");
            }
        }

        PropertyLinkage linkage = PropertyLinkage.builder()
                .agent(agent)
                .property(property)
                .unit(unit)
                .tenantLeadName(request.getTenantLeadName())
                .tenantLeadPhone(request.getTenantLeadPhone())
                .tenantLeadEmail(request.getTenantLeadEmail())
                .notes(request.getNotes())
                .status(LinkageStatus.PENDING)
                .build();

        linkage = linkageRepository.save(linkage);
        auditService.log("LINKAGE_CREATED", "PropertyLinkage", linkage.getId(), null);
        log.info("Agent {} created linkage {} for property {}", agentId, linkage.getId(), property.getId());
        return toLinkageResponse(linkage);
    }

    @Transactional(readOnly = true)
    public Page<LinkageResponse> getMyLinkages(Long agentId, Pageable pageable) {
        return linkageRepository.findByAgentIdOrderByCreatedAtDesc(agentId, pageable)
                .map(this::toLinkageResponse);
    }

    @Transactional(readOnly = true)
    public LinkageResponse getLinkage(Long linkageId, Long agentId) {
        PropertyLinkage linkage = linkageRepository.findById(linkageId)
                .orElseThrow(() -> RentMISException.notFound("Linkage not found"));
        if (!linkage.getAgent().getId().equals(agentId)) {
            throw RentMISException.forbidden("Access denied");
        }
        return toLinkageResponse(linkage);
    }

    // ── Commission view ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<CommissionResponse> getMyCommissions(Long agentId, Pageable pageable) {
        return commissionRepository.findByAgentIdOrderByCreatedAtDesc(agentId, pageable)
                .map(this::toCommissionResponse);
    }

    // ── Internal: called by ContractServiceImpl when contract goes ACTIVE ────

    /**
     * Auto-trigger: when a contract is signed for a given property+unit,
     * find any PENDING/ACCEPTED linkages and mark them CONTRACT_SIGNED,
     * then create a commission record for the agent.
     */
    @Transactional
    public void onContractSigned(Long propertyId, Long unitId, String propertyName,
                                  String district, String province, String unitRef) {
        List<PropertyLinkage> linkages =
                linkageRepository.findActiveForPropertyUnit(propertyId, unitId);

        for (PropertyLinkage linkage : linkages) {
            linkage.setStatus(LinkageStatus.CONTRACT_SIGNED);
            linkageRepository.save(linkage);

            // Only create commission if one doesn't already exist
            if (commissionRepository.findByLinkageId(linkage.getId()).isEmpty()) {
                String propRef = buildPropertyRef(propertyName, district, province);
                Commission commission = Commission.builder()
                        .linkage(linkage)
                        .agent(linkage.getAgent())
                        .propertyRef(propRef)
                        .propertyId(propertyId)
                        .unitRef(unitRef)
                        .status(CommissionStatus.PENDING)
                        .build();
                commissionRepository.save(commission);
                log.info("Commission auto-created for agent {} linkage {}",
                        linkage.getAgent().getId(), linkage.getId());
            }
        }
    }

    // ── Public agent credibility profile ────────────────────────────────────

    @Transactional(readOnly = true)
    public AgentProfileResponse getAgentPublicProfile(Long agentId) {
        User agent = userRepository.findById(agentId)
                .filter(u -> u.getRole() == com.rentmis.model.enums.Role.AGENT
                          && Boolean.TRUE.equals(u.getIsActive()))
                .orElseThrow(() -> RentMISException.notFound("Agent not found"));

        AgentProfileResponse r = new AgentProfileResponse();
        r.setId(agent.getId());
        r.setFullName(agent.getFullName());
        r.setPhone(agent.getPhone());
        r.setProfileImage(agent.getProfileImage());
        r.setIsVerified(Boolean.TRUE.equals(agent.getIsVerified()));
        r.setSignedContracts(linkageRepository.countByAgentIdAndStatus(agentId, LinkageStatus.CONTRACT_SIGNED));
        r.setPaidCommissions(commissionRepository.countByAgentIdAndStatus(agentId, CommissionStatus.PAID));
        r.setTotalCommissions(commissionRepository.countByAgentId(agentId));
        r.setJoinedAt(agent.getCreatedAt());
        return r;
    }

    // ── Admin: manage linkage status + commissions ───────────────────────────

    @Transactional
    public LinkageResponse updateLinkageStatus(Long linkageId, LinkageStatus newStatus,
                                                String notes, Long adminId) {
        PropertyLinkage linkage = linkageRepository.findById(linkageId)
                .orElseThrow(() -> RentMISException.notFound("Linkage not found"));

        linkage.setStatus(newStatus);
        linkage.setReviewedNotes(notes);
        linkage.setReviewedAt(java.time.LocalDateTime.now());
        linkage = linkageRepository.save(linkage);
        auditService.log("LINKAGE_STATUS_UPDATED", "PropertyLinkage", linkageId, null);
        return toLinkageResponse(linkage);
    }

    @Transactional
    public CommissionResponse updateCommissionStatus(Long commissionId, CommissionStatus newStatus,
                                                      java.math.BigDecimal amount, String notes) {
        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> RentMISException.notFound("Commission not found"));

        commission.setStatus(newStatus);
        if (amount != null) commission.setAmount(amount);
        if (notes != null)  commission.setAdminNotes(notes);
        if (newStatus == CommissionStatus.APPROVED) commission.setApprovedAt(java.time.LocalDateTime.now());
        if (newStatus == CommissionStatus.PAID)     commission.setPaidAt(java.time.LocalDateTime.now());

        commission = commissionRepository.save(commission);
        auditService.log("COMMISSION_STATUS_UPDATED", "Commission", commissionId, null);
        return toCommissionResponse(commission);
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private AgentPropertyResponse toAgentPropertyResponse(Property p) {
        List<Unit> available = p.getUnits().stream()
                .filter(u -> Boolean.TRUE.equals(u.getIsActive()) && u.getStatus() == UnitStatus.AVAILABLE)
                .collect(Collectors.toList());

        BigDecimal minRent = available.stream()
                .map(Unit::getRentAmount)
                .filter(r -> r != null)
                .min(Comparator.naturalOrder()).orElse(null);

        BigDecimal maxRent = available.stream()
                .map(Unit::getRentAmount)
                .filter(r -> r != null)
                .max(Comparator.naturalOrder()).orElse(null);

        List<AgentPropertyResponse.AgentUnitResponse> unitList = available.stream()
                .map(u -> {
                    AgentPropertyResponse.AgentUnitResponse ur = new AgentPropertyResponse.AgentUnitResponse();
                    ur.setId(u.getId());
                    ur.setUnitNumber(u.getUnitNumber());
                    ur.setFloorNumber(u.getFloorNumber());
                    ur.setUnitType(u.getUnitType());
                    ur.setUnitPurpose(u.getUnitPurpose() != null ? u.getUnitPurpose().name() : null);
                    ur.setRentAmount(u.getRentAmount());
                    ur.setAreaSqm(u.getAreaSqm());
                    ur.setNumBedrooms(u.getNumBedrooms());
                    ur.setNumBathrooms(u.getNumBathrooms());
                    ur.setWingName(u.getWing() != null ? u.getWing().getName() : null);
                    return ur;
                }).collect(Collectors.toList());

        AgentPropertyResponse r = new AgentPropertyResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setCategory(p.getCategory());
        r.setLandUse(p.getLandUse());
        r.setDistrict(p.getDistrict());
        r.setSector(p.getSector());
        r.setProvince(p.getProvince());
        r.setDescription(p.getDescription());
        r.setAvailableUnits(available.size());
        r.setMinRent(minRent);
        r.setMaxRent(maxRent);
        r.setUnits(unitList);
        return r;
    }

    private LinkageResponse toLinkageResponse(PropertyLinkage l) {
        LinkageResponse r = new LinkageResponse();
        r.setId(l.getId());
        r.setPropertyId(l.getProperty().getId());
        r.setPropertyRef(buildPropertyRef(
                l.getProperty().getName(),
                l.getProperty().getDistrict(),
                l.getProperty().getProvince()));
        if (l.getUnit() != null) {
            r.setUnitId(l.getUnit().getId());
            r.setUnitRef(buildUnitRef(l.getUnit()));
        }
        r.setTenantLeadName(l.getTenantLeadName());
        r.setTenantLeadPhone(l.getTenantLeadPhone());
        r.setStatus(l.getStatus());
        r.setNotes(l.getNotes());
        r.setReviewedNotes(l.getReviewedNotes());
        r.setReviewedAt(l.getReviewedAt());
        r.setCreatedAt(l.getCreatedAt());
        if (l.getCommission() != null) {
            r.setCommissionStatus(l.getCommission().getStatus());
            r.setCommissionId(l.getCommission().getId());
        }
        return r;
    }

    private CommissionResponse toCommissionResponse(Commission c) {
        CommissionResponse r = new CommissionResponse();
        r.setId(c.getId());
        r.setLinkageId(c.getLinkage().getId());
        r.setPropertyRef(c.getPropertyRef());
        r.setPropertyId(c.getPropertyId());
        r.setUnitRef(c.getUnitRef());
        r.setStatus(c.getStatus());
        r.setAmount(c.getAmount());
        r.setCurrency(c.getCurrency());
        r.setApprovedAt(c.getApprovedAt());
        r.setPaidAt(c.getPaidAt());
        r.setCreatedAt(c.getCreatedAt());
        return r;
    }

    private String buildPropertyRef(String name, String district, String province) {
        StringBuilder sb = new StringBuilder();
        if (name != null) sb.append(name);
        if (district != null || province != null) {
            sb.append(" — ");
            if (district != null) sb.append(district);
            if (province != null) sb.append(", ").append(province);
        }
        return sb.toString();
    }

    private String buildUnitRef(Unit u) {
        StringBuilder sb = new StringBuilder("Unit ").append(u.getUnitNumber());
        if (u.getFloorNumber() != null) sb.append(", Floor ").append(u.getFloorNumber());
        return sb.toString();
    }
}
