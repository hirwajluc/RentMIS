package com.rentmis.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices", indexes = {
        @Index(name = "idx_invoices_payment", columnList = "payment_id"),
        @Index(name = "idx_invoices_number", columnList = "invoice_number")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Invoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;

    @Column(name = "invoice_number", nullable = false, unique = true, length = 100)
    private String invoiceNumber;

    @Column(name = "ebm_invoice_number", length = 100)
    private String ebmInvoiceNumber;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "tax_amount", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 10)
    @Builder.Default
    private String currency = "RWF";

    // EBM Response fields
    @Column(name = "ebm_verification_code", length = 500)
    private String ebmVerificationCode;

    @Column(name = "qr_code", length = 2000)
    private String qrCode;

    @Column(name = "pdf_url", length = 500)
    private String pdfUrl;

    @Column(name = "ebm_status", length = 30)
    private String ebmStatus;

    @Column(name = "ebm_response", columnDefinition = "TEXT")
    private String ebmResponse;

    @Column(name = "ebm_submitted_at")
    private LocalDateTime ebmSubmittedAt;

    @Column(name = "ebm_retry_count")
    @Builder.Default
    private Integer ebmRetryCount = 0;

    @Column(name = "verification_url", length = 500)
    private String verificationUrl;
}
