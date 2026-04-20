package com.rentmis.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotNull
    private Long unitId;

    @NotNull @Positive
    private BigDecimal amount;

    @NotNull @Min(1) @Max(12)
    private Integer paymentPeriodMonth;

    @NotNull @Min(2000)
    private Integer paymentPeriodYear;

    @Size(max = 200)
    private String notes;

    // For idempotency
    private String idempotencyKey;
}
