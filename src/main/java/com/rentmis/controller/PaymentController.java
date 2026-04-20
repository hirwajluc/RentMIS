package com.rentmis.controller;

import com.rentmis.dto.request.BulkPaymentRequest;
import com.rentmis.dto.request.ManualPaymentRequest;
import com.rentmis.dto.request.PaymentRequest;
import com.rentmis.dto.response.ApiResponse;
import com.rentmis.dto.response.PaymentResponse;
import com.rentmis.exception.RentMISException;
import com.rentmis.model.enums.PaymentStatus;
import com.rentmis.security.jwt.CustomUserDetails;
import com.rentmis.service.impl.PaymentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceImpl paymentService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> listPayments(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) PaymentStatus status,
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(size = 20) Pageable pageable) {

        Long landlordId = null;
        String role = user.getRole().name();

        if ("TENANT".equals(role)) {
            // Tenant sees only their own payments — ignore any passed tenantId
            tenantId   = user.getUserId();
            landlordId = null;
        } else if ("LANDLORD".equals(role)) {
            // Landlord sees only payments on their own properties
            landlordId = user.getUserId();
            tenantId   = null; // don't double-filter unless admin explicitly passes one
        }
        // ADMIN: no forced filter — sees everything

        return ResponseEntity.ok(ApiResponse.ok(
                paymentService.getPayments(tenantId, unitId, landlordId, status, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(
                paymentService.getPayment(id, user.getRole().name(), user.getUserId())));
    }

    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Payment initiated",
                        paymentService.initiatePayment(request, user.getUserId())));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> initiateMultiPayment(
            @Valid @RequestBody BulkPaymentRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Multi-month payment initiated",
                        paymentService.initiateMultiPayment(request, user.getUserId())));
    }

    @GetMapping("/by-ref/{reference}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByReference(
            @PathVariable String reference) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentByReference(reference)));
    }

    @PostMapping("/verify-by-ref/{reference}")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyByReference(
            @PathVariable String reference) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.verifyAndCompleteByReference(reference)));
    }

    @PostMapping("/verify/{transactionId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(
            @PathVariable String transactionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                paymentService.verifyAndCompletePayment(transactionId)));
    }

    // ── Manual Payments ──────────────────────────────────────────────────────

    /** Tenant initiates a Cash or Bank Transfer payment */
    @PostMapping("/manual")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> initiateManualPayment(
            @Valid @RequestBody ManualPaymentRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Manual payment initiated",
                        paymentService.initiateManualPayment(request, user.getUserId())));
    }

    /** Tenant uploads bank receipt for a PENDING bank transfer payment */
    @PostMapping(value = "/{id}/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ApiResponse<PaymentResponse>> uploadReceipt(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Receipt uploaded",
                paymentService.uploadReceipt(id, file, user.getUserId())));
    }

    /** Download/view receipt file — accessible by owner tenant and their landlord */
    @GetMapping("/{id}/receipt-file")
    public ResponseEntity<Resource> getReceiptFile(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) throws IOException {
        Path filePath = paymentService.getReceiptFilePath(id, user.getUserId(), user.getRole().name());
        Resource resource = new PathResource(filePath);
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) contentType = "application/octet-stream";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    /** Landlord confirms a manual (Cash or Bank Transfer) payment */
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails user) {
        String note     = body != null ? body.get("note")     : null;
        String password = body != null ? body.get("password") : null;
        return ResponseEntity.ok(ApiResponse.ok("Payment confirmed",
                paymentService.confirmManualPayment(id, user.getUserId(), note, password)));
    }

    /** Landlord rejects a manual payment */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponse<PaymentResponse>> rejectPayment(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails user) {
        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(ApiResponse.ok("Payment rejected",
                paymentService.rejectManualPayment(id, user.getUserId(), reason)));
    }

    /** Landlord records one or more cash payments directly (marks them COMPLETED immediately) */
    @PostMapping("/cash")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> recordCash(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal CustomUserDetails user) {
        Long unitId = Long.valueOf(body.get("unitId").toString());
        BigDecimal amount = new BigDecimal(body.get("amountPerMonth").toString());
        String notes    = body.containsKey("notes")    ? (String) body.get("notes")    : null;
        String password = body.containsKey("password") ? (String) body.get("password") : null;

        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> periods =
                (java.util.List<Map<String, Object>>) body.get("periods");

        java.util.List<PaymentResponse> results = new java.util.ArrayList<>();
        for (Map<String, Object> pe : periods) {
            Integer month = Integer.valueOf(pe.get("month").toString());
            Integer year  = Integer.valueOf(pe.get("year").toString());
            results.add(paymentService.recordCashPayment(unitId, month, year, amount, notes, user.getUserId(), password));
        }
        return ResponseEntity.status(201).body(ApiResponse.ok(
                "Cash payment" + (results.size() > 1 ? "s" : "") + " recorded", results));
    }

    /** Landlord gets all payments waiting for their confirmation */
    @GetMapping("/pending-confirmation")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> pendingConfirmations(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(
                paymentService.getPendingConfirmations(user.getUserId())));
    }

    // GLSPay Webhook - public endpoint
    @PostMapping("/webhook/glspay")
    public ResponseEntity<Map<String, String>> glspayWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-GLSPay-Signature", defaultValue = "") String signature) {
        try {
            paymentService.handleGLSPayWebhook(payload, signature);
            return ResponseEntity.ok(Map.of("status", "received"));
        } catch (Exception e) {
            log.error("Webhook processing error: {}", e.getMessage());
            return ResponseEntity.ok(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
