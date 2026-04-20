package com.rentmis.controller;

import com.rentmis.dto.response.ApiResponse;
import com.rentmis.dto.response.DashboardResponse;
import com.rentmis.security.jwt.CustomUserDetails;
import com.rentmis.service.impl.DashboardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardServiceImpl dashboardService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @AuthenticationPrincipal CustomUserDetails user) {
        Long landlordId = "LANDLORD".equals(user.getRole().name()) ? user.getUserId() : null;
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getDashboard(landlordId)));
    }
}
