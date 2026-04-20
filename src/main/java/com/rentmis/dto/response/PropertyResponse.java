package com.rentmis.dto.response;

import com.rentmis.model.enums.LandUse;
import com.rentmis.model.enums.PropertyCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PropertyResponse {
    private Long id;
    private String name;

    // Category & land use
    private PropertyCategory category;
    private LandUse landUse;

    // Legacy
    private String propertyType;

    // Rwanda address
    private String address;
    private String province;
    private String city;
    private String district;
    private String sector;
    private String cell;
    private String village;
    private String upi;

    private String description;

    // Wings
    private Boolean hasWings;

    // Full House
    private Integer numBedrooms;
    private Integer numBathrooms;
    private Integer parkingSpaces;
    private BigDecimal houseRentAmount;
    private BigDecimal houseAreaSqm;

    // Area management
    private BigDecimal totalAreaSqm;
    private BigDecimal usedAreaSqm;

    // Derived stats
    private Integer totalUnits;
    private Long occupiedUnits;
    private Long availableUnits;
    private Integer floors; // max(unit.floorNumber)

    private String imageUrl;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isActive;
    private UserResponse landlord;
    private LocalDateTime createdAt;
}
