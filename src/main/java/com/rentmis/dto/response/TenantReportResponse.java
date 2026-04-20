package com.rentmis.dto.response;

import com.rentmis.model.enums.ReportStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TenantReportResponse {
    private Long id;
    private Long tenantId;
    private String tenantName;
    private String tenantEmail;
    private Long reportedById;
    private String reportedByName;
    private String reason;
    private ReportStatus status;
    private String adminNotes;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
}
