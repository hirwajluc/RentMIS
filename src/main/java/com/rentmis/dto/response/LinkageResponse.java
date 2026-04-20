package com.rentmis.dto.response;

import com.rentmis.model.enums.CommissionStatus;
import com.rentmis.model.enums.LinkageStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Agent-facing linkage response.
 * Deliberately OMITS: landlord identity, tenant User identity, contract details.
 */
@Data
public class LinkageResponse {
    private Long id;

    // Property reference only — no landlord identity
    private Long propertyId;
    private String propertyRef;   // "Name — District, Province"
    private Long unitId;
    private String unitRef;       // "Unit A-101, Floor 2"

    // Lead info (visible only to creating agent)
    private String tenantLeadName;
    private String tenantLeadPhone;

    private LinkageStatus status;
    private String notes;
    private String reviewedNotes;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;

    // Commission summary (if it exists)
    private CommissionStatus commissionStatus;
    private Long commissionId;
}
