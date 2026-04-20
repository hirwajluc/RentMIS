package com.rentmis.controller;

import com.rentmis.dto.response.ApiResponse;
import com.rentmis.dto.response.TenantReportResponse;
import com.rentmis.dto.response.UserResponse;
import com.rentmis.model.enums.Language;
import com.rentmis.model.enums.ReportStatus;
import com.rentmis.model.enums.Role;
import com.rentmis.security.jwt.CustomUserDetails;
import com.rentmis.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> listUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUsers(role, search, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUser(id)));
    }

    @PutMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.toggleActive(id)));
    }

    @GetMapping("/available-tenants")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAvailableTenants() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAvailableTenants()));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails user) {
        Language lang = null;
        if (body.get("language") != null) {
            try { lang = Language.valueOf(body.get("language").toUpperCase()); } catch (IllegalArgumentException ignored) {}
        }
        return ResponseEntity.ok(ApiResponse.ok(userService.updateProfile(
                user.getUserId(),
                body.get("firstName"),
                body.get("lastName"),
                body.get("phone"),
                body.get("address"),
                lang)));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails user) {
        userService.changePassword(user.getUserId(), body.get("oldPassword"), body.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.ok("Password changed", null));
    }

    // ── Tenant reporting ──────────────────────────────────────────────────────

    @PostMapping("/{tenantId}/report")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<TenantReportResponse>> reportTenant(
            @PathVariable Long tenantId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.status(201).body(ApiResponse.ok("Report submitted",
                userService.reportTenant(tenantId, user.getUserId(), body.get("reason"))));
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<TenantReportResponse>>> getReports(
            @RequestParam(required = false) ReportStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getReports(status, pageable)));
    }

    @GetMapping("/{tenantId}/reports")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<List<TenantReportResponse>>> getTenantReports(
            @PathVariable Long tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getReportsForTenant(tenantId)));
    }

    @PutMapping("/reports/{reportId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TenantReportResponse>> reviewReport(
            @PathVariable Long reportId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails user) {
        ReportStatus decision = ReportStatus.valueOf(body.get("decision").toUpperCase());
        return ResponseEntity.ok(ApiResponse.ok("Report reviewed",
                userService.reviewReport(reportId, user.getUserId(), decision, body.get("notes"))));
    }
}
