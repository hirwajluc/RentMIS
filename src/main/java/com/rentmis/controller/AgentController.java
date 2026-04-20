package com.rentmis.controller;

import com.rentmis.dto.request.LinkageRequest;
import com.rentmis.dto.response.*;
import com.rentmis.model.enums.CommissionStatus;
import com.rentmis.model.enums.LinkageStatus;
import com.rentmis.security.jwt.CustomUserDetails;
import com.rentmis.service.impl.AgentServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentServiceImpl agentService;

    // ── Dashboard ────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<ApiResponse<AgentDashboardResponse>> dashboard(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(agentService.getDashboard(user.getUserId())));
    }

    // ── Vacant properties (black-box view — no landlord identity) ────────────

    @GetMapping("/properties")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<ApiResponse<Page<AgentPropertyResponse>>> vacantProperties(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(agentService.getVacantProperties(search, pageable)));
    }

    @GetMapping("/properties/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<ApiResponse<AgentPropertyResponse>> vacantProperty(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(agentService.getVacantProperty(id)));
    }

    // ── Linkages ─────────────────────────────────────────────────────────────

    @PostMapping("/linkages")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<ApiResponse<LinkageResponse>> createLinkage(
            @Valid @RequestBody LinkageRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Linkage created",
                        agentService.createLinkage(request, user.getUserId())));
    }

    @GetMapping("/linkages")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<ApiResponse<Page<LinkageResponse>>> myLinkages(
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                agentService.getMyLinkages(user.getUserId(), pageable)));
    }

    @GetMapping("/linkages/{id}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<ApiResponse<LinkageResponse>> getLinkage(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(
                agentService.getLinkage(id, user.getUserId())));
    }

    // ── Commissions ──────────────────────────────────────────────────────────

    @GetMapping("/commissions")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<ApiResponse<Page<CommissionResponse>>> myCommissions(
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                agentService.getMyCommissions(user.getUserId(), pageable)));
    }

    // ── Public credibility profile (no auth required) ────────────────────────

    @GetMapping("/{id}/public-profile")
    public ResponseEntity<ApiResponse<AgentProfileResponse>> publicProfile(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(agentService.getAgentPublicProfile(id)));
    }

    // ── Admin endpoints: manage linkage status + commissions ─────────────────

    @PutMapping("/admin/linkages/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LinkageResponse>> updateLinkageStatus(
            @PathVariable Long id,
            @RequestParam LinkageStatus status,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated",
                agentService.updateLinkageStatus(id, status, notes, user.getUserId())));
    }

    @PutMapping("/admin/commissions/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CommissionResponse>> updateCommissionStatus(
            @PathVariable Long id,
            @RequestParam CommissionStatus status,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(ApiResponse.ok("Commission updated",
                agentService.updateCommissionStatus(id, status, amount, notes)));
    }
}
