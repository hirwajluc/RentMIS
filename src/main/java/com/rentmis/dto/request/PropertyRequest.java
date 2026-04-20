package com.rentmis.dto.request;

import com.rentmis.model.enums.LandUse;
import com.rentmis.model.enums.PropertyCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertyRequest {

    // Category (required for new properties; optional for legacy edits)
    private PropertyCategory category;

    // Land use — auto-derived from category but can be overridden
    private LandUse landUse;

    @NotBlank @Size(max = 200)
    private String name;

    @NotBlank @Size(max = 500)
    private String address;

    // Rwanda address chain
    @Size(max = 100)
    private String province;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String district;

    @Size(max = 100)
    private String sector;

    @Size(max = 100)
    private String cell;

    @Size(max = 100)
    private String village;

    @Size(max = 100)
    private String upi;

    private String description;

    // Wings
    private Boolean hasWings;

    // Legacy type field
    @Size(max = 50)
    private String propertyType;

    // Full House specific
    @Min(0) @Max(20)
    private Integer numBedrooms;

    @Min(0) @Max(10)
    private Integer numBathrooms;

    @Min(0) @Max(50)
    private Integer parkingSpaces;

    @Positive
    private BigDecimal houseRentAmount;

    @Positive
    private BigDecimal houseAreaSqm;

    // Total area allocated for unit management (APARTMENT/COMPLEX)
    @Positive
    private BigDecimal totalAreaSqm;

    // Geo
    private BigDecimal latitude;
    private BigDecimal longitude;
}
