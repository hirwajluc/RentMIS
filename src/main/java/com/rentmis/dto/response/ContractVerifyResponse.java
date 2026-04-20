package com.rentmis.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class ContractVerifyResponse {
    // Overall result
    private String status;          // VALID | TAMPERED | PARTIAL | PENDING | NOT_FOUND
    private boolean valid;
    private String message;

    // Contract identity
    private String contractNumber;
    private String contractStatus;  // ACTIVE | TERMINATED | etc.

    // Integrity checks
    private String storedHash;
    private String recomputedHash;
    private boolean hashMatch;

    private String blockchainRef;
    private String recomputedBlockchainRef;
    private boolean blockchainRefMatch;

    // Signatures
    private boolean landlordSigned;
    private boolean tenantSigned;
    private LocalDateTime landlordSignedAt;
    private LocalDateTime tenantSignedAt;

    // Contract summary (public-safe fields)
    private String tenantName;
    private String landlordName;
    private String propertyName;
    private String unitNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String monthlyRent;
}
