package com.rentmis.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentDashboardResponse {
    private long totalLinkages;
    private long pendingLinkages;
    private long acceptedLinkages;
    private long rejectedLinkages;
    private long contractSignedLinkages;
    private long expiredLinkages;
    private long totalCommissions;
    private long pendingCommissions;
    private long approvedCommissions;
    private long paidCommissions;
}
