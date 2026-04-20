package com.rentmis.model.entity;

import com.rentmis.model.enums.UnitPurpose;
import com.rentmis.model.enums.UnitStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "units", indexes = {
        @Index(name = "idx_units_property", columnList = "property_id"),
        @Index(name = "idx_units_status", columnList = "status"),
        @Index(name = "idx_units_tenant", columnList = "current_tenant_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Unit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_number", nullable = false, length = 50)
    private String unitNumber;

    @Column(name = "floor_number")
    private Integer floorNumber;

    // Legacy string type (STUDIO, 1BR, 2BR …) — kept for backward compat
    @Column(name = "unit_type", length = 50)
    private String unitType;

    // Purpose: RESIDENTIAL / SHOP / OFFICE / RESTAURANT
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_purpose", length = 20)
    private UnitPurpose unitPurpose;

    @Column(name = "rent_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal rentAmount;

    @Column(name = "deposit_amount", precision = 12, scale = 2)
    private BigDecimal depositAmount;

    @Column(name = "area_sqm", precision = 8, scale = 2)
    private BigDecimal areaSqm;

    // Price per m² — only used for COMPLEX properties; rent = pricePerSqm × areaSqm
    @Column(name = "price_per_sqm", precision = 12, scale = 2)
    private BigDecimal pricePerSqm;

    @Column(name = "num_bedrooms")
    private Integer numBedrooms;

    @Column(name = "num_bathrooms")
    private Integer numBathrooms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UnitStatus status = UnitStatus.AVAILABLE;

    @Column(name = "amenities", columnDefinition = "TEXT")
    private String amenities;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    // Optional wing / block assignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wing_id")
    private Wing wing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_tenant_id")
    private User currentTenant;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Contract> contracts = new ArrayList<>();

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();
}
