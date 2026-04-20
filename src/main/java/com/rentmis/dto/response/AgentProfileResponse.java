package com.rentmis.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AgentProfileResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String profileImage;   // base64 JPEG from NIDA — may be null
    private Boolean isVerified;    // true when NIDA returned a photo
    private long signedContracts;  // linkages that reached CONTRACT_SIGNED
    private long paidCommissions;  // commissions that were paid out
    private long totalCommissions; // all commissions (credibility breadth)
    private LocalDateTime joinedAt;
}
