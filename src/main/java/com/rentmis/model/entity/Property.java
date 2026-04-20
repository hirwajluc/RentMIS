package com.rentmis.model.entity;

import com.rentmis.model.enums.LandUse;
import com.rentmis.model.enums.PropertyCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties", indexes = {
        @Index(name = "idx_properties_landlord", columnList = "landlord_id"),
        @Index(name = "idx_properties_status", columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Property extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    // --- Property Category & Land Use ---
    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30)
    private PropertyCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "land_use", length = 20)
    private LandUse landUse;

    // --- Legacy type field (kept for backward compat) ---
    @Column(name = "property_type", length = 50)
    private String propertyType;

    // --- Rwanda Address ---
    @Column(nullable = false, length = 500)
    private String address; // free-text street / plot description

    @Column(length = 100)
    private String province;

    @Column(length = 100)
    private String city; // kept for backward compat

    @Column(length = 100)
    private String district;

    @Column(length = 100)
    private String sector;

    @Column(length = 100)
    private String cell;

    @Column(length = 100)
    private String village;

    @Column(name = "upi", length = 100)
    private String upi; // Unique Parcel Identifier

    // --- Description ---
    @Column(columnDefinition = "TEXT")
    private String description;

    // --- Wings ---
    @Column(name = "has_wings")
    @Builder.Default
    private Boolean hasWings = false;

    // --- Full-House specific fields ---
    @Column(name = "num_bedrooms")
    private Integer numBedrooms;

    @Column(name = "num_bathrooms")
    private Integer numBathrooms;

    @Column(name = "parking_spaces")
    private Integer parkingSpaces;

    @Column(name = "house_rent_amount", precision = 12, scale = 2)
    private BigDecimal houseRentAmount;

    @Column(name = "house_area_sqm", precision = 8, scale = 2)
    private BigDecimal houseAreaSqm;

    // --- Total area available across all units/floors ---
    @Column(name = "total_area_sqm", precision = 10, scale = 2)
    private BigDecimal totalAreaSqm;

    // --- Derived counters (kept for convenience / legacy) ---
    @Column(name = "total_units")
    @Builder.Default
    private Integer totalUnits = 0;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private User landlord;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Unit> units = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Wing> wings = new ArrayList<>();
}
