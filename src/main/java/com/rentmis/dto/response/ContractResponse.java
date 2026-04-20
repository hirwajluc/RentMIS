package com.rentmis.dto.response;

import com.rentmis.model.enums.ContractStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ContractResponse {
    private Long id;
    private String contractNumber;
    private UserResponse tenant;
    private UserResponse landlord;
    private UnitResponse unit;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal monthlyRent;
    private BigDecimal depositAmount;
    private ContractStatus status;
    private String termsConditions;
    private String specialClauses;
    private LocalDateTime landlordSignedAt;
    private LocalDateTime tenantSignedAt;
    private String landlordSignature;
    private String tenantSignature;
    private String contractHash;
    private String blockchainTxHash;
    private String blockchainNetwork;
    private LocalDateTime blockchainTimestamp;
    private String pdfUrl;
    private LocalDateTime createdAt;
    private LocalDateTime tamperDetectedAt;
}
