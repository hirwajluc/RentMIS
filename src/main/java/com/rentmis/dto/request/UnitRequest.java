package com.rentmis.dto.request;

import com.rentmis.model.enums.UnitPurpose;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UnitRequest {
    @NotNull
    private Long propertyId;

    @NotBlank @Size(max = 50)
    private String unitNumber;

    private Integer floorNumber;

    // Legacy size/layout type (STUDIO, 1BR …) — still accepted
    @Size(max = 50)
    private String unitType;

    // Purpose: RESIDENTIAL / SHOP / OFFICE / RESTAURANT
    private UnitPurpose unitPurpose;

    // Optional wing assignment
    private Long wingId;

    @NotNull @Positive
    private BigDecimal rentAmount;

    @PositiveOrZero
    private BigDecimal depositAmount;

    @Positive
    private BigDecimal areaSqm;

    // Optional — only for COMPLEX properties. When provided, rent = pricePerSqm × areaSqm
    @Positive
    private BigDecimal pricePerSqm;

    @Min(0) @Max(20)
    private Integer numBedrooms;

    @Min(0) @Max(10)
    private Integer numBathrooms;

    private String amenities;
}
