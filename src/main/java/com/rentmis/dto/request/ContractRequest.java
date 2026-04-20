package com.rentmis.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractRequest {
    @NotNull
    private Long tenantId;

    @NotNull
    private Long unitId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull @Positive
    private BigDecimal monthlyRent;

    @PositiveOrZero
    private BigDecimal depositAmount;

    private String termsConditions;

    private String specialClauses;
}
