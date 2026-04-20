package com.rentmis.model.entity;

import com.rentmis.model.enums.ContractStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts", indexes = {
        @Index(name = "idx_contracts_tenant", columnList = "tenant_id"),
        @Index(name = "idx_contracts_unit", columnList = "unit_id"),
        @Index(name = "idx_contracts_status", columnList = "status"),
        @Index(name = "idx_contracts_hash", columnList = "contract_hash")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Contract extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_number", nullable = false, unique = true, length = 50)
    private String contractNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private User landlord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "monthly_rent", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyRent;

    @Column(name = "deposit_amount", precision = 12, scale = 2)
    private BigDecimal depositAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ContractStatus status = ContractStatus.DRAFT;

    @Column(name = "terms_conditions", columnDefinition = "LONGTEXT")
    private String termsConditions;

    @Column(name = "special_clauses", columnDefinition = "TEXT")
    private String specialClauses;

    // Digital signatures
    @Column(name = "landlord_signed_at")
    private LocalDateTime landlordSignedAt;

    @Column(name = "tenant_signed_at")
    private LocalDateTime tenantSignedAt;

    @Column(name = "landlord_signature_ip", length = 45)
    private String landlordSignatureIp;

    @Column(name = "tenant_signature_ip", length = 45)
    private String tenantSignatureIp;

    // Blockchain / cryptographic integrity fields
    @Column(name = "contract_hash", length = 64)
    private String contractHash; // SHA-256 of contract data

    @Column(name = "landlord_signature", length = 128)
    private String landlordSignature; // HMAC-SHA256 of landlord signing

    @Column(name = "tenant_signature", length = 128)
    private String tenantSignature; // HMAC-SHA256 of tenant signing

    @Column(name = "blockchain_tx_hash", length = 100)
    private String blockchainTxHash; // blockchain_ref = SHA256(hash|landlordSig|tenantSig)[:32]

    @Column(name = "blockchain_network", length = 50)
    private String blockchainNetwork;

    @Column(name = "blockchain_timestamp")
    private LocalDateTime blockchainTimestamp;

    @Column(name = "blockchain_block_number")
    private Long blockchainBlockNumber;

    // Tamper detection enforcement
    @Column(name = "tamper_detected_at")
    private LocalDateTime tamperDetectedAt;

    // Termination
    @Column(name = "terminated_at")
    private LocalDateTime terminatedAt;

    @Column(name = "termination_reason", columnDefinition = "TEXT")
    private String terminationReason;

    @Column(name = "pdf_url", length = 500)
    private String pdfUrl;
}
