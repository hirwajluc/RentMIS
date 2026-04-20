package com.rentmis.model.entity;

import com.rentmis.model.enums.LinkageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A brokerage linkage created by an Agent connecting a vacant property/unit
 * with a tenant lead.
 *
 * Privacy design:
 *  - tenantLead fields are plain strings (no FK to User) — agent cannot
 *    query or join tenant identity from this record.
 *  - No landlord reference exposed beyond the property FK.
 */
@Entity
@Table(name = "property_linkages", indexes = {
        @Index(name = "idx_linkage_agent",    columnList = "agent_id"),
        @Index(name = "idx_linkage_property", columnList = "property_id"),
        @Index(name = "idx_linkage_unit",     columnList = "unit_id"),
        @Index(name = "idx_linkage_status",   columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PropertyLinkage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The agent who created this linkage. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private User agent;

    /** The property being recommended. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    /** Specific unit recommended (optional). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    /** Tenant lead — stored as plain strings, NOT a FK to User. */
    @Column(name = "tenant_lead_name", nullable = false, length = 200)
    private String tenantLeadName;

    @Column(name = "tenant_lead_phone", nullable = false, length = 30)
    private String tenantLeadPhone;

    @Column(name = "tenant_lead_email", length = 200)
    private String tenantLeadEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LinkageStatus status = LinkageStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    /** Populated by admin when updating status. */
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reviewed_notes", length = 500)
    private String reviewedNotes;

    /** One commission record, auto-created when status → CONTRACT_SIGNED. */
    @OneToOne(mappedBy = "linkage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Commission commission;
}
