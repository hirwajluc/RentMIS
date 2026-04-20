package com.rentmis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentmis.dto.response.ApiResponse;
import com.rentmis.dto.response.InvoiceResponse;
import com.rentmis.exception.RentMISException;
import com.rentmis.integration.ebm.EBMInvoiceResponse;
import com.rentmis.integration.ebm.EBMService;
import com.rentmis.mapper.InvoiceMapper;
import com.rentmis.model.entity.Invoice;
import com.rentmis.model.entity.Payment;
import com.rentmis.repository.InvoiceRepository;
import com.rentmis.repository.PaymentRepository;
import com.rentmis.security.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceMapper invoiceMapper;
    private final EBMService ebmService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> listInvoices(
            @RequestParam(required = false) String ebmStatus,
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(size = 20, sort = "issuedAt") Pageable pageable) {

        Long tenantId = "TENANT".equals(user.getRole().name()) ? user.getUserId() : null;
        Page<InvoiceResponse> page = invoiceRepository
                .filterInvoices(tenantId, ebmStatus, pageable)
                .map(invoiceMapper::toResponse);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoice(@PathVariable Long id) {
        Invoice inv = invoiceRepository.findById(id)
                .orElseThrow(() -> RentMISException.notFound("Invoice not found"));
        return ResponseEntity.ok(ApiResponse.ok(invoiceMapper.toResponse(inv)));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> stats() {
        long total   = invoiceRepository.count();
        long success = invoiceRepository.countByEbmStatus("SUCCESS");
        long pending = invoiceRepository.countByEbmStatus("PENDING");
        long failed  = invoiceRepository.countByEbmStatus("FAILED");
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "total", total, "success", success, "pending", pending, "failed", failed)));
    }

    /** Create an invoice record for a completed payment — landlord/admin only */
    @PostMapping("/for-payment/{paymentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    @Transactional
    public ResponseEntity<ApiResponse<InvoiceResponse>> createForPayment(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal CustomUserDetails user) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> RentMISException.notFound("Payment not found"));

        if (payment.getStatus() != com.rentmis.model.enums.PaymentStatus.COMPLETED) {
            throw RentMISException.badRequest("Invoice can only be created for a completed payment");
        }
        if (payment.getInvoice() != null) {
            return ResponseEntity.ok(ApiResponse.ok("Invoice already exists", invoiceMapper.toResponse(payment.getInvoice())));
        }

        java.math.BigDecimal vatAmount = payment.getTotalAmount()
                .multiply(new java.math.BigDecimal("0.18"))
                .divide(new java.math.BigDecimal("1.18"), 2, java.math.RoundingMode.HALF_UP);
        java.math.BigDecimal netAmount = payment.getTotalAmount().subtract(vatAmount);

        Invoice invoice = Invoice.builder()
                .payment(payment)
                .invoiceNumber(com.rentmis.util.ReferenceGenerator.generateInvoiceNumber())
                .issuedAt(LocalDateTime.now())
                .amount(netAmount)
                .taxAmount(vatAmount)
                .totalAmount(payment.getTotalAmount())
                .currency(payment.getCurrency())
                .ebmStatus("PENDING")
                .build();

        invoice = invoiceRepository.save(invoice);
        return ResponseEntity.ok(ApiResponse.ok("Invoice created", invoiceMapper.toResponse(invoice)));
    }

    /** Manually retry EBM submission — admin/landlord only */
    @PostMapping("/{id}/retry-ebm")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    @Transactional
    public ResponseEntity<ApiResponse<InvoiceResponse>> retryEbm(@PathVariable Long id) {
        return doGenerateEbm(id);
    }

    /** Generate EBM for an invoice — admin/landlord only. */
    @PostMapping("/{id}/generate-ebm")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    @Transactional
    public ResponseEntity<ApiResponse<InvoiceResponse>> generateEbm(@PathVariable Long id) {
        return doGenerateEbm(id);
    }

    private ResponseEntity<ApiResponse<InvoiceResponse>> doGenerateEbm(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> RentMISException.notFound("Invoice not found"));

        if ("SUCCESS".equals(invoice.getEbmStatus()) && invoice.getEbmVerificationCode() != null) {
            return ResponseEntity.ok(ApiResponse.ok("EBM already generated", invoiceMapper.toResponse(invoice)));
        }

        // Recovery: status is SUCCESS but verCode was never saved — try to extract from stored raw response
        if ("SUCCESS".equals(invoice.getEbmStatus()) && invoice.getEbmResponse() != null) {
            String recovered = extractIntrlDataFromResponse(invoice.getEbmResponse());
            if (recovered != null) {
                invoice.setEbmVerificationCode(recovered);
                invoice.setQrCode(recovered);
                invoice.setVerificationUrl("https://qa.inkomane.rw/ebm/" + recovered);
                invoiceRepository.save(invoice);
                return ResponseEntity.ok(ApiResponse.ok("EBM already generated", invoiceMapper.toResponse(invoice)));
            }
        }

        Payment payment = paymentRepository.findById(invoice.getPayment().getId())
                .orElseThrow(() -> RentMISException.notFound("Payment not found"));

        EBMInvoiceResponse ebmResp = ebmService.submitInvoice(payment, invoice);

        invoice.setEbmInvoiceNumber(ebmResp.getEbmInvoiceNumber());
        invoice.setEbmVerificationCode(ebmResp.getEbmVerificationCode());
        invoice.setQrCode(ebmResp.getQrCode());
        invoice.setVerificationUrl(ebmResp.getVerificationUrl());
        invoice.setEbmStatus(ebmResp.getStatus());
        invoice.setEbmResponse(ebmResp.getRawResponse());
        invoice.setEbmSubmittedAt(LocalDateTime.now());
        invoice.setEbmRetryCount(invoice.getEbmRetryCount() + 1);
        invoiceRepository.save(invoice);

        if (!"SUCCESS".equals(ebmResp.getStatus())) {
            String reason = ebmResp.getErrorMessage() != null
                    ? ebmResp.getErrorMessage()
                    : "EBM server did not return a success response. Please try again.";
            return ResponseEntity.status(502)
                    .body(ApiResponse.error(reason));
        }

        return ResponseEntity.ok(ApiResponse.ok("EBM generated successfully", invoiceMapper.toResponse(invoice)));
    }

    /** Try to extract intrlData (verification code) from a stored raw EBM receipt JSON response. */
    private String extractIntrlDataFromResponse(String rawJson) {
        try {
            var root = objectMapper.readTree(rawJson);
            // data.ebmVerificationCode
            var vc = root.path("data").path("ebmVerificationCode");
            if (vc.isTextual() && !vc.asText().isBlank()) return vc.asText();
            // data.response.data.intrlData
            var intrl = root.path("data").path("response").path("data").path("intrlData");
            if (intrl.isTextual() && !intrl.asText().isBlank()) return intrl.asText();
        } catch (Exception ignored) {}
        return null;
    }
}
