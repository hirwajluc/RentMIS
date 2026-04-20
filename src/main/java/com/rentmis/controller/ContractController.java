package com.rentmis.controller;

import com.rentmis.dto.request.ContractRequest;
import com.rentmis.dto.response.ApiResponse;
import com.rentmis.dto.response.ContractResponse;
import com.rentmis.dto.response.ContractVerifyResponse;
import com.rentmis.model.enums.ContractStatus;
import com.rentmis.security.jwt.CustomUserDetails;
import com.rentmis.service.impl.ContractServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractServiceImpl contractService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ContractResponse>>> listContracts(
            @RequestParam(required = false) ContractStatus status,
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(size = 20) Pageable pageable) {

        Long tenantId = "TENANT".equals(user.getRole().name()) ? user.getUserId() : null;
        Long landlordId = "LANDLORD".equals(user.getRole().name()) ? user.getUserId() : null;

        return ResponseEntity.ok(ApiResponse.ok(
                contractService.getContracts(tenantId, landlordId, status, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractResponse>> getContract(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(contractService.getContract(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<ContractResponse>> createContract(
            @Valid @RequestBody ContractRequest request,
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Contract created",
                        contractService.createContract(request, user.getUserId(), ip)));
    }

    @PostMapping("/{id}/sign/landlord")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<ContractResponse>> landlordSign(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest httpRequest) {
        String password = body.get("password");
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Password is required"));
        }
        return ResponseEntity.ok(ApiResponse.ok("Contract signed",
                contractService.signContract(id, "LANDLORD", httpRequest.getRemoteAddr(),
                        userDetails.getUserId(), password)));
    }

    @PostMapping("/{id}/sign/tenant")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ApiResponse<ContractResponse>> tenantSign(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest httpRequest) {
        String password = body.get("password");
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Password is required"));
        }
        return ResponseEntity.ok(ApiResponse.ok("Contract signed",
                contractService.signContract(id, "TENANT", httpRequest.getRemoteAddr(),
                        userDetails.getUserId(), password)));
    }

    @PostMapping("/{id}/terminate")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<ContractResponse>> terminateContract(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ApiResponse.ok("Contract terminated",
                contractService.terminateContract(id, body.get("reason"), httpRequest.getRemoteAddr())));
    }

    @GetMapping("/{id}/verify")
    public ResponseEntity<ApiResponse<ContractResponse>> verifyIntegrity(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Integrity verified",
                contractService.verifyContractIntegrity(id)));
    }

    /** Public — verify by contract number (no auth required) */
    @GetMapping("/verify/number/{contractNumber}")
    public ResponseEntity<ApiResponse<ContractVerifyResponse>> verifyByNumber(
            @PathVariable String contractNumber) {
        return ResponseEntity.ok(ApiResponse.ok(contractService.verifyByContractNumber(contractNumber)));
    }

    /** Public — verify by blockchain reference hash (no auth required) */
    @GetMapping("/verify/ref/{blockchainRef}")
    public ResponseEntity<ApiResponse<ContractVerifyResponse>> verifyByRef(
            @PathVariable String blockchainRef) {
        return ResponseEntity.ok(ApiResponse.ok(contractService.verifyByBlockchainRef(blockchainRef)));
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdf = contractService.generateContractPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"contract-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
