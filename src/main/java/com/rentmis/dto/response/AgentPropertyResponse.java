package com.rentmis.dto.response;

import com.rentmis.model.enums.LandUse;
import com.rentmis.model.enums.PropertyCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Privacy-safe property view for agents.
 * Deliberately OMITS: landlord identity, landlord contact, full address, UPI, coordinates.
 */
@Data
public class AgentPropertyResponse {
    private Long id;
    private String name;
    private PropertyCategory category;
    private LandUse landUse;

    // Location — district/province only, no street address
    private String district;
    private String sector;
    private String province;

    private String description;

    // Available unit summary — no per-unit tenant/landlord data
    private int availableUnits;
    private BigDecimal minRent;
    private BigDecimal maxRent;

    // Available units list (safe view only)
    private List<AgentUnitResponse> units;

    @Data
    public static class AgentUnitResponse {
        private Long id;
        private String unitNumber;
        private Integer floorNumber;
        private String unitType;
        private String unitPurpose;
        private BigDecimal rentAmount;
        private BigDecimal areaSqm;
        private Integer numBedrooms;
        private Integer numBathrooms;
        private String wingName;
    }
}
