package com.rentmis.dto.response;

import com.rentmis.model.enums.UnitPurpose;
import com.rentmis.model.enums.UnitStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UnitResponse {
    private Long id;
    private String unitNumber;
    private Integer floorNumber;
    private String unitType;
    private UnitPurpose unitPurpose;
    private BigDecimal rentAmount;
    private BigDecimal depositAmount;
    private BigDecimal areaSqm;
    private BigDecimal pricePerSqm;
    private Integer numBedrooms;
    private Integer numBathrooms;
    private UnitStatus status;
    private String amenities;
    private Long propertyId;
    private String propertyName;
    private Long wingId;
    private String wingName;
    private UserResponse currentTenant;
    private LocalDateTime createdAt;
}
