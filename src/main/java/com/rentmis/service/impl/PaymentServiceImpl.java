package com.rentmis.service.impl;

import com.rentmis.audit.AuditService;
import com.rentmis.dto.request.BulkPaymentRequest;
import com.rentmis.dto.request.ManualPaymentRequest;
import com.rentmis.dto.request.PaymentRequest;
import com.rentmis.dto.response.PaymentResponse;
import com.rentmis.exception.RentMISException;
import com.rentmis.integration.glspay.GLSPayCheckoutResponse;
import com.rentmis.integration.glspay.GLSPayService;
import com.rentmis.integration.glspay.GLSPayStatusResponse;
import com.rentmis.mapper.PaymentMapper;
import com.rentmis.model.entity.Contract;
import com.rentmis.model.entity.Invoice;
import com.rentmis.model.entity.Payment;
import com.rentmis.model.entity.Unit;
import com.rentmis.model.entity.User;
import com.rentmis.model.enums.ContractStatus;
import com.rentmis.model.enums.PaymentStatus;
import com.rentmis.repository.ContractRepository;
import com.rentmis.repository.InvoiceRepository;
import com.rentmis.repository.PaymentRepository;
import com.rentmis.repository.UnitRepository;
import com.rentmis.repository.UserRepository;
import com.rentmis.security.jwt.CustomUserDetails;
import com.rentmis.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl {

    private final PaymentRepository paymentRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;
    private final GLSPayService glsPayService;
    private final PaymentMapper paymentMapper;
    private final AuditService auditService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url:http://localhost:5050}")
    private String baseUrl;

    @Value("${app.upload-dir:uploads/receipts}")
    private String uploadDir;

    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request, Long tenantId) {
        // Idempotency check
        if (request.getIdempotencyKey() != null) {
            paymentRepository.findByIdempotencyKey(request.getIdempotencyKey())
                    .ifPresent(existing -> {
                        throw RentMISException.conflict("Duplicate payment request");
                    });
        }

        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> RentMISException.notFound("Tenant not found"));

        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> RentMISException.notFound("Unit not found"));

        // Check tenant is assigned to this unit
        if (unit.getCurrentTenant() == null || !unit.getCurrentTenant().getId().equals(tenantId)) {
            throw RentMISException.forbidden("You are not assigned to this unit");
        }

        // Check for duplicate payment this period
        List<Payment> existing = paymentRepository
                .findByTenantIdAndPaymentPeriodMonthAndPaymentPeriodYear(
                        tenantId, request.getPaymentPeriodMonth(), request.getPaymentPeriodYear());
        existing.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .findFirst()
                .ifPresent(p -> { throw RentMISException.conflict("Rent already paid for this period"); });
        existing.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING
                          || p.getStatus() == PaymentStatus.PROCESSING)
                .findFirst()
                .ifPresent(p -> { throw RentMISException.conflict(
                        "A payment for this period is already in progress (status: " + p.getStatus() + ")"); });

        // Find active contract
        Contract contract = contractRepository
                .findByUnitIdAndStatusIn(unit.getId(), List.of(ContractStatus.ACTIVE))
                .orElse(null);

        BigDecimal amount = request.getAmount();
        BigDecimal total = amount; // penalties could be added here

        String reference = ReferenceGenerator.generatePaymentRef();
        String idempotencyKey = request.getIdempotencyKey() != null
                ? request.getIdempotencyKey()
                : UUID.randomUUID().toString();

        Payment payment = Payment.builder()
                .referenceNumber(reference)
                .tenant(tenant)
                .unit(unit)
                .contract(contract)
                .amount(amount)
                .penaltyAmount(BigDecimal.ZERO)
                .totalAmount(total)
                .currency("RWF")
                .status(PaymentStatus.PENDING)
                .paymentMethod("GLSPAY")
                .paymentPeriodMonth(request.getPaymentPeriodMonth())
                .paymentPeriodYear(request.getPaymentPeriodYear())
                .notes(request.getNotes())
                .idempotencyKey(idempotencyKey)
                .build();

        payment = paymentRepository.save(payment);

        // Initiate GLSPay checkout
        try {
            String callbackUrl = baseUrl + "/api/payments/webhook/glspay";
            String returnUrl = baseUrl + "/html/tenant/payments.html";

            GLSPayCheckoutResponse checkout = glsPayService.initiatePayment(payment, callbackUrl, returnUrl);
            payment.setGlspayTransactionId(checkout.getTransactionId());
            payment.setGlspayCheckoutUrl(checkout.getCheckoutUrl());
            payment.setStatus(PaymentStatus.PROCESSING);
            payment = paymentRepository.save(payment);

        } catch (Exception e) {
            log.error("GLSPay initiation failed for {}: {}", reference, e.getMessage());
        }

        auditService.log("PAYMENT_INITIATED", "Payment", payment.getId(), null);
        return paymentMapper.toResponse(payment);
    }

    @Transactional
    public List<PaymentResponse> initiateMultiPayment(BulkPaymentRequest request, Long tenantId) {
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> RentMISException.notFound("Tenant not found"));
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> RentMISException.notFound("Unit not found"));

        if (unit.getCurrentTenant() == null || !unit.getCurrentTenant().getId().equals(tenantId)) {
            throw RentMISException.forbidden("You are not assigned to this unit");
        }

        // Validate every requested period
        for (BulkPaymentRequest.PeriodEntry pe : request.getPeriods()) {
            List<Payment> existing = paymentRepository
                    .findByTenantIdAndPaymentPeriodMonthAndPaymentPeriodYear(tenantId, pe.getMonth(), pe.getYear());
            existing.stream().filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                    .findFirst().ifPresent(p -> { throw RentMISException.conflict(
                            "Rent already paid for " + pe.getMonth() + "/" + pe.getYear()); });
            existing.stream().filter(p -> p.getStatus() == PaymentStatus.PENDING
                                       || p.getStatus() == PaymentStatus.PROCESSING)
                    .findFirst().ifPresent(p -> { throw RentMISException.conflict(
                            "Payment in progress for " + pe.getMonth() + "/" + pe.getYear()); });
        }

        Contract contract = contractRepository
                .findByUnitIdAndStatusIn(unit.getId(), List.of(ContractStatus.ACTIVE))
                .orElse(null);

        BigDecimal amountPerMonth = request.getAmountPerMonth();
        BigDecimal totalAmount = amountPerMonth.multiply(new BigDecimal(request.getPeriods().size()));
        String sharedIdempotencyKey = request.getIdempotencyKey() != null
                ? request.getIdempotencyKey() : UUID.randomUUID().toString();

        // Create one Payment record per period — all sharing the same GLSPay transaction
        java.util.List<Payment> payments = new java.util.ArrayList<>();
        for (BulkPaymentRequest.PeriodEntry pe : request.getPeriods()) {
            Payment p = Payment.builder()
                    .referenceNumber(ReferenceGenerator.generatePaymentRef())
                    .tenant(tenant)
                    .unit(unit)
                    .contract(contract)
                    .amount(amountPerMonth)
                    .penaltyAmount(BigDecimal.ZERO)
                    .totalAmount(amountPerMonth)
                    .currency("RWF")
                    .status(PaymentStatus.PENDING)
                    .paymentMethod("GLSPAY")
                    .paymentPeriodMonth(pe.getMonth())
                    .paymentPeriodYear(pe.getYear())
                    .notes(request.getNotes())
                    .idempotencyKey(sharedIdempotencyKey + "-" + pe.getYear() + "-" + pe.getMonth())
                    .build();
            payments.add(paymentRepository.save(p));
        }

        // Single GLSPay checkout for the combined total using the first payment as anchor
        Payment anchor = payments.get(0);
        // Temporarily set totalAmount so GLSPay charges the correct combined total
        anchor.setTotalAmount(totalAmount);
        try {
            String callbackUrl = baseUrl + "/api/payments/webhook/glspay";
            String returnUrl   = baseUrl + "/html/tenant/payments.html";
            GLSPayCheckoutResponse checkout = glsPayService.initiatePayment(anchor, callbackUrl, returnUrl);
            String txId = checkout.getTransactionId();
            String checkoutUrl = checkout.getCheckoutUrl();
            // Stamp all payments with the same GLSPay transaction so the webhook can resolve them all.
            // Reset anchor totalAmount back to per-month so every record stores its own share.
            for (Payment p : payments) {
                p.setTotalAmount(amountPerMonth);
                p.setGlspayTransactionId(txId);
                p.setGlspayCheckoutUrl(checkoutUrl);
                p.setStatus(PaymentStatus.PROCESSING);
                paymentRepository.save(p);
            }
        } catch (Exception e) {
            log.error("GLSPay multi-payment initiation failed: {}", e.getMessage());
        }

        auditService.log("MULTI_PAYMENT_INITIATED", "Payment", anchor.getId(), null);
        return payments.stream().map(paymentMapper::toResponse).toList();
    }

    // ── Manual payments (Cash / Bank Transfer) ──────────────────────────────

    @Transactional
    public List<PaymentResponse> initiateManualPayment(ManualPaymentRequest request, Long tenantId) {
        String method = request.getPaymentMethod().toUpperCase();
        if (!"CASH".equals(method) && !"BANK_TRANSFER".equals(method)) {
            throw RentMISException.badRequest("Invalid payment method. Use CASH or BANK_TRANSFER");
        }

        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> RentMISException.notFound("Tenant not found"));
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> RentMISException.notFound("Unit not found"));

        if (unit.getCurrentTenant() == null || !unit.getCurrentTenant().getId().equals(tenantId)) {
            throw RentMISException.forbidden("You are not assigned to this unit");
        }

        for (ManualPaymentRequest.PeriodEntry pe : request.getPeriods()) {
            List<Payment> existing = paymentRepository.findByTenantIdAndPaymentPeriodMonthAndPaymentPeriodYear(
                    tenantId, pe.getMonth(), pe.getYear());
            existing.stream().filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                    .findFirst().ifPresent(p -> { throw RentMISException.conflict(
                            "Rent already paid for " + pe.getMonth() + "/" + pe.getYear()); });
            existing.stream().filter(p -> p.getStatus() == PaymentStatus.PENDING
                                       || p.getStatus() == PaymentStatus.PROCESSING
                                       || p.getStatus() == PaymentStatus.PENDING_CONFIRMATION)
                    .findFirst().ifPresent(p -> { throw RentMISException.conflict(
                            "Payment in progress for " + pe.getMonth() + "/" + pe.getYear()); });
        }

        Contract contract = contractRepository
                .findByUnitIdAndStatusIn(unit.getId(), List.of(ContractStatus.ACTIVE))
                .orElse(null);

        String sharedKey = request.getIdempotencyKey() != null
                ? request.getIdempotencyKey() : UUID.randomUUID().toString();

        // Cash: PENDING_CONFIRMATION immediately (landlord will confirm)
        // Bank Transfer: PENDING (tenant still needs to upload receipt)
        PaymentStatus initialStatus = "CASH".equals(method)
                ? PaymentStatus.PENDING_CONFIRMATION : PaymentStatus.PENDING;

        List<Payment> payments = new java.util.ArrayList<>();
        for (ManualPaymentRequest.PeriodEntry pe : request.getPeriods()) {
            Payment p = Payment.builder()
                    .referenceNumber(ReferenceGenerator.generatePaymentRef())
                    .tenant(tenant)
                    .unit(unit)
                    .contract(contract)
                    .amount(request.getAmountPerMonth())
                    .penaltyAmount(BigDecimal.ZERO)
                    .totalAmount(request.getAmountPerMonth())
                    .currency("RWF")
                    .status(initialStatus)
                    .paymentMethod(method)
                    .paymentPeriodMonth(pe.getMonth())
                    .paymentPeriodYear(pe.getYear())
                    .notes(request.getNotes())
                    .idempotencyKey(sharedKey + "-" + pe.getYear() + "-" + pe.getMonth())
                    .build();
            payments.add(paymentRepository.save(p));
        }

        auditService.log("MANUAL_PAYMENT_INITIATED", "Payment", payments.get(0).getId(), null);
        return payments.stream().map(paymentMapper::toResponse).toList();
    }

    @Transactional
    public PaymentResponse uploadReceipt(Long paymentId, MultipartFile file, Long tenantId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> RentMISException.notFound("Payment not found"));
        if (!payment.getTenant().getId().equals(tenantId)) {
            throw RentMISException.forbidden("Access denied");
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw RentMISException.badRequest("Receipt can only be uploaded for PENDING bank transfer payments");
        }
        if (!"BANK_TRANSFER".equals(payment.getPaymentMethod())) {
            throw RentMISException.badRequest("Receipt upload is only for Bank Transfer payments");
        }

        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String storedName = "receipt-" + payment.getReferenceNumber() + "-" + System.currentTimeMillis() + ext;

        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            Path dest = dir.resolve(storedName);
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw RentMISException.badRequest("Failed to save receipt file: " + e.getMessage());
        }

        payment.setReceiptFilePath(storedName);
        payment.setReceiptOriginalName(original);
        payment.setStatus(PaymentStatus.PENDING_CONFIRMATION);
        payment = paymentRepository.save(payment);

        auditService.log("RECEIPT_UPLOADED", "Payment", payment.getId(), null);
        return paymentMapper.toResponse(payment);
    }

    @Transactional
    public PaymentResponse confirmManualPayment(Long paymentId, Long landlordId, String note, String password) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> RentMISException.notFound("Payment not found"));

        verifyLandlordOwnsPayment(payment, landlordId);

        if (payment.getStatus() != PaymentStatus.PENDING_CONFIRMATION) {
            throw RentMISException.badRequest("Payment is not awaiting confirmation");
        }

        User landlord = userRepository.findById(landlordId)
                .orElseThrow(() -> RentMISException.notFound("Landlord not found"));

        if (password == null || password.isBlank() || !passwordEncoder.matches(password, landlord.getPassword())) {
            throw RentMISException.forbidden("Incorrect password");
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        payment.setConfirmedBy(landlord);
        payment.setConfirmedAt(LocalDateTime.now());
        payment.setActionNote(note);
        payment = paymentRepository.save(payment);

        auditService.log("MANUAL_PAYMENT_CONFIRMED", "Payment", payment.getId(), null);
        return paymentMapper.toResponse(payment);
    }

    @Transactional
    public PaymentResponse rejectManualPayment(Long paymentId, Long landlordId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> RentMISException.notFound("Payment not found"));

        verifyLandlordOwnsPayment(payment, landlordId);

        if (payment.getStatus() != PaymentStatus.PENDING_CONFIRMATION) {
            throw RentMISException.badRequest("Payment is not awaiting confirmation");
        }

        User landlord = userRepository.findById(landlordId)
                .orElseThrow(() -> RentMISException.notFound("Landlord not found"));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setConfirmedBy(landlord);
        payment.setConfirmedAt(LocalDateTime.now());
        payment.setActionNote(reason);
        payment = paymentRepository.save(payment);

        auditService.log("MANUAL_PAYMENT_REJECTED", "Payment", payment.getId(), null);
        return paymentMapper.toResponse(payment);
    }

    @Transactional
    public PaymentResponse recordCashPayment(Long unitId, Integer month, Integer year,
                                              BigDecimal amount, String notes, Long landlordId, String password) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> RentMISException.notFound("Unit not found"));

        // Verify landlord owns this unit's property
        if (unit.getProperty() == null || unit.getProperty().getLandlord() == null
                || !unit.getProperty().getLandlord().getId().equals(landlordId)) {
            throw RentMISException.forbidden("You do not own this unit");
        }
        if (unit.getCurrentTenant() == null) {
            throw RentMISException.badRequest("No tenant assigned to this unit");
        }

        User tenant = unit.getCurrentTenant();
        List<Payment> existing = paymentRepository.findByTenantIdAndPaymentPeriodMonthAndPaymentPeriodYear(
                tenant.getId(), month, year);
        existing.stream().filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .findFirst().ifPresent(p -> { throw RentMISException.conflict("Rent already paid for this period"); });

        User landlord = userRepository.findById(landlordId)
                .orElseThrow(() -> RentMISException.notFound("Landlord not found"));

        if (password == null || password.isBlank() || !passwordEncoder.matches(password, landlord.getPassword())) {
            throw RentMISException.forbidden("Incorrect password");
        }

        Contract contract = contractRepository
                .findByUnitIdAndStatusIn(unit.getId(), List.of(ContractStatus.ACTIVE))
                .orElse(null);

        Payment payment = Payment.builder()
                .referenceNumber(ReferenceGenerator.generatePaymentRef())
                .tenant(tenant)
                .unit(unit)
                .contract(contract)
                .amount(amount)
                .penaltyAmount(BigDecimal.ZERO)
                .totalAmount(amount)
                .currency("RWF")
                .status(PaymentStatus.COMPLETED)
                .paymentMethod("CASH")
                .paymentPeriodMonth(month)
                .paymentPeriodYear(year)
                .notes(notes)
                .paidAt(LocalDateTime.now())
                .confirmedBy(landlord)
                .confirmedAt(LocalDateTime.now())
                .idempotencyKey(UUID.randomUUID().toString())
                .build();

        payment = paymentRepository.save(payment);
        auditService.log("CASH_PAYMENT_RECORDED", "Payment", payment.getId(), null);
        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPendingConfirmations(Long landlordId) {
        return paymentRepository.findPendingConfirmationByLandlord(landlordId)
                .stream().map(paymentMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Path getReceiptFilePath(Long paymentId, Long requesterId, String role) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> RentMISException.notFound("Payment not found"));
        if ("TENANT".equals(role) && !payment.getTenant().getId().equals(requesterId)) {
            throw RentMISException.forbidden("Access denied");
        }
        if ("LANDLORD".equals(role)) {
            verifyLandlordOwnsPayment(payment, requesterId);
        }
        if (payment.getReceiptFilePath() == null) {
            throw RentMISException.notFound("No receipt on file for this payment");
        }
        return Paths.get(uploadDir).resolve(payment.getReceiptFilePath());
    }

    private void verifyLandlordOwnsPayment(Payment payment, Long landlordId) {
        Long ownerLandlordId = payment.getUnit() != null
                && payment.getUnit().getProperty() != null
                && payment.getUnit().getProperty().getLandlord() != null
                ? payment.getUnit().getProperty().getLandlord().getId() : null;
        if (!landlordId.equals(ownerLandlordId)) {
            throw RentMISException.forbidden("Access denied");
        }
    }

    @Transactional
    public void handleGLSPayWebhook(Map<String, Object> payload, String signature) {
        // GLSPay uses polling — signature header is informational only
        String transactionId = (String) payload.get("transaction_id");
        String status = (String) payload.get("status");
        String reference = (String) payload.get("reference");

        // Resolve all payments sharing this transaction (multi-month scenario).
        // Always expand by transactionId so sibling payments in the same bulk checkout are included.
        java.util.Set<Long> seen = new java.util.HashSet<>();
        List<Payment> affected = new java.util.ArrayList<>();
        if (reference != null) {
            paymentRepository.findByReferenceNumber(reference).ifPresent(p -> {
                if (seen.add(p.getId())) affected.add(p);
            });
        }
        if (transactionId != null) {
            paymentRepository.findAllByGlspayTransactionId(transactionId).forEach(p -> {
                if (seen.add(p.getId())) affected.add(p);
            });
        }
        if (affected.isEmpty()) throw RentMISException.notFound("Payment not found");

        LocalDateTime now = LocalDateTime.now();
        for (Payment payment : affected) {
            payment.setGlspaySignatureVerified(true);
            if ("COMPLETED".equalsIgnoreCase(status) || "SUCCESS".equalsIgnoreCase(status)) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaidAt(now);
                paymentRepository.save(payment);
            } else if ("FAILED".equalsIgnoreCase(status)) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            } else {
                paymentRepository.save(payment);
            }
            auditService.log("PAYMENT_WEBHOOK", "Payment", payment.getId(), null);
        }
    }

    @Async
    @Transactional
    public void triggerInvoiceGeneration(Payment payment) {
        try {
            // 18% VAT: VAT = amount × 18/118
            BigDecimal vatAmount = payment.getTotalAmount()
                    .multiply(new BigDecimal("0.18"))
                    .divide(new BigDecimal("1.18"), 2, java.math.RoundingMode.HALF_UP);
            BigDecimal netAmount = payment.getTotalAmount().subtract(vatAmount);

            String invoiceNumber = ReferenceGenerator.generateInvoiceNumber();

            Invoice invoice = Invoice.builder()
                    .payment(payment)
                    .invoiceNumber(invoiceNumber)
                    .issuedAt(LocalDateTime.now())
                    .amount(netAmount)
                    .taxAmount(vatAmount)
                    .totalAmount(payment.getTotalAmount())
                    .currency(payment.getCurrency())
                    .ebmStatus("PENDING")
                    .build();

            invoiceRepository.save(invoice);
            log.info("Invoice {} created (PENDING EBM) for payment {}", invoiceNumber, payment.getReferenceNumber());

        } catch (Exception e) {
            log.error("Invoice generation failed for payment {}: {}", payment.getReferenceNumber(), e.getMessage());
        }
    }

    @Transactional
    public PaymentResponse verifyAndCompletePayment(String transactionId) {
        List<Payment> siblings = paymentRepository.findAllByGlspayTransactionId(transactionId);
        if (siblings.isEmpty()) throw RentMISException.notFound("Payment not found");

        GLSPayStatusResponse status = glsPayService.verifyPayment(transactionId);

        if ("COMPLETED".equalsIgnoreCase(status.getStatus())) {
            LocalDateTime now = LocalDateTime.now();
            for (Payment p : siblings) {
                if (p.getStatus() != PaymentStatus.COMPLETED) {
                    p.setStatus(PaymentStatus.COMPLETED);
                    p.setPaidAt(now);
                    paymentRepository.save(p);
                }
            }
        }

        return paymentMapper.toResponse(siblings.get(0));
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPayments(Long tenantId, Long unitId, Long landlordId,
                                              PaymentStatus status, Pageable pageable) {
        return paymentRepository.filterPayments(tenantId, unitId, landlordId, status, null, null, pageable)
                .map(paymentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long id, String role, Long userId) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> RentMISException.notFound("Payment not found"));
        if ("TENANT".equals(role) && !payment.getTenant().getId().equals(userId)) {
            throw RentMISException.forbidden("Access denied");
        }
        if ("LANDLORD".equals(role)) {
            Long landlordId = payment.getUnit().getProperty() != null
                    && payment.getUnit().getProperty().getLandlord() != null
                    ? payment.getUnit().getProperty().getLandlord().getId() : null;
            if (!userId.equals(landlordId)) throw RentMISException.forbidden("Access denied");
        }
        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByReference(String reference) {
        return paymentMapper.toResponse(
                paymentRepository.findByReferenceNumber(reference)
                        .orElseThrow(() -> RentMISException.notFound("Payment not found")));
    }

    @Transactional
    public PaymentResponse verifyAndCompleteByReference(String reference) {
        Payment payment = paymentRepository.findByReferenceNumber(reference)
                .orElseThrow(() -> RentMISException.notFound("Payment not found"));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            return paymentMapper.toResponse(payment);
        }

        if (payment.getGlspayTransactionId() != null) {
            GLSPayStatusResponse status = glsPayService.verifyPayment(payment.getGlspayTransactionId());
            if ("COMPLETED".equalsIgnoreCase(status.getStatus())) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaidAt(LocalDateTime.now());
                paymentRepository.save(payment);
            } else if ("FAILED".equalsIgnoreCase(status.getStatus())
                    || "CANCELLED".equalsIgnoreCase(status.getStatus())) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }
        }
        return paymentMapper.toResponse(payment);
    }
}
