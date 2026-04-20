package com.rentmis.controller;

import com.rentmis.dto.response.ApiResponse;
import com.rentmis.dto.response.ReportData;
import com.rentmis.security.jwt.CustomUserDetails;
import com.rentmis.service.impl.ReportServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
public class ReportController {

    private final ReportServiceImpl reportService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ReportData>> getSummary(
            @AuthenticationPrincipal CustomUserDetails user) {
        Long landlordId = "LANDLORD".equals(user.getRole().name()) ? user.getUserId() : null;
        return ResponseEntity.ok(ApiResponse.ok(reportService.buildReport(landlordId)));
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> getPdf(
            @AuthenticationPrincipal CustomUserDetails user) {
        try {
            Long landlordId = "LANDLORD".equals(user.getRole().name()) ? user.getUserId() : null;
            ReportData data = reportService.buildReport(landlordId);
            data.setGeneratedBy(user.getFullName());

            byte[] pdf = reportService.generatePdf(data, user.getFullName());

            String filename = "RentMIS-Report-" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")) + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            log.error("Report PDF generation failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
