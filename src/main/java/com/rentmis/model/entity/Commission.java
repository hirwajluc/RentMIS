package com.rentmis.model.entity;

import com.rentmis.model.enums.CommissionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Commission record auto-created when an agent's linkage leads to a signed contract.
 *
 * Privacy design:
 *  - Links ONLY to the agent (User) and the linkage record.
 *  - propertyRef / propertyId are stored as immutable snapshots — no active FK
 *    to Property entity is exposed in agent-facing responses.
 *  - No tenant or landlord identity is stored here.
 */
@Entity
@Table(name = "commissions", indexes = {
        @Index(name = "idx_commission_agent",   columnList = "agent_id"),
        @Index(name = "idx_commission_status",  columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Commission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linkage_id", nullable = false, unique = true)
    private PropertyLinkage linkage;

    /** The agent who earns the commission. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private User agent;

    /**
     * Immutable snapshot of the property reference at time of commission creation.
     * Allows commission records to remain valid even after property changes.
     */
    @Column(name = "property_ref", nullable = false, length = 300)
    private String propertyRef;   // e.g. "Kacyiru Apartments — Gasabo, Kigali"

    @Column(name = "property_id")
    private Long propertyId;      // numeric reference only, not a FK

    @Column(name = "unit_ref", length = 100)
    private String unitRef;       // e.g. "Unit A-101, Floor 2"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CommissionStatus status = CommissionStatus.PENDING;

    /** Amount set by admin when approving. */
    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", length = 10)
    @Builder.Default
    private String currency = "RWF";

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
