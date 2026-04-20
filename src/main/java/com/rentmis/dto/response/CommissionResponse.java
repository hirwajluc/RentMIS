package com.rentmis.dto.response;

import com.rentmis.model.enums.CommissionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Agent-facing commission response.
 * Contains ONLY agent-safe fields: no tenant or landlord identity.
 */
@Data
public class CommissionResponse {
    private Long id;
    private Long linkageId;
    private String propertyRef;
    private Long propertyId;
    private String unitRef;
    private CommissionStatus status;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime approvedAt;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
