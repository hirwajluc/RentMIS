package com.rentmis.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ManualPaymentRequest {

    @NotNull
    private Long unitId;

    /** CASH or BANK_TRANSFER */
    @NotBlank
    private String paymentMethod;

    @NotNull @NotEmpty
    private List<PeriodEntry> periods;

    @NotNull @Positive
    private BigDecimal amountPerMonth;

    @Size(max = 300)
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
