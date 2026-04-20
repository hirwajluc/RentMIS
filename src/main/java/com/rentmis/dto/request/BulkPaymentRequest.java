package com.rentmis.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BulkPaymentRequest {

    @NotNull
    private Long unitId;

    /** One entry per month being paid. */
    @NotNull @NotEmpty
    private List<PeriodEntry> periods;

    /** Rent amount per month (used to compute total). */
    @NotNull @Positive
    private BigDecimal amountPerMonth;

    @Size(max = 200)
    private String notes;

    private String idempotencyKey;

    @Data
    public static class PeriodEntry {
        @NotNull @Min(1) @Max(12)
        private Integer month;
        @NotNull @Min(2000)
        private Integer year;
    }
}
