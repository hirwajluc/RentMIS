package com.rentmis.model.entity;

import com.rentmis.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payments_tenant", columnList = "tenant_id"),
        @Index(name = "idx_payments_unit", columnList = "unit_id"),
        @Index(name = "idx_payments_status", columnList = "status"),
        @Index(name = "idx_payments_reference", columnList = "reference_number"),
        @Index(name = "idx_payments_period", columnList = "payment_period_month, payment_period_year")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_number", nullable = false, unique = true, length = 100)
    private String referenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "penalty_amount", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal penaltyAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 10)
    @Builder.Default
    private String currency = "RWF";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // GLSPAY, MOMO, BANK_TRANSFER, CASH

    @Column(name = "payment_period_month")
    private Integer paymentPeriodMonth;

    @Column(name = "payment_period_year")
    private Integer paymentPeriodYear;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // GLSPay fields
    @Column(name = "glspay_transaction_id", length = 200)
    private String glspayTransactionId;

    @Column(name = "glspay_checkout_url", length = 1000)
    private String glspayCheckoutUrl;

    @Column(name = "glspay_webhook_data", columnDefinition = "TEXT")
    private String glspayWebhookData;

    @Column(name = "glspay_signature_verified")
    @Builder.Default
    private Boolean glspaySignatureVerified = false;

    // EBM fields
    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Invoice invoice;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Replay attack prevention
    @Column(name = "idempotency_key", length = 100, unique = true)
    private String idempotencyKey;

    // Manual payment fields (Cash / Bank Transfer)
    @Column(name = "receipt_file_path", length = 500)
    private String receiptFilePath;

    @Column(name = "receipt_original_name", length = 255)
    private String receiptOriginalName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by")
    private User confirmedBy;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "action_note", length = 500)
    private String actionNote;
}
