package com.rentmis.integration.glspay;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data @Builder
public class GLSPayStatusResponse {
    private String transactionId;
    private String status; // COMPLETED, PENDING, FAILED
    private BigDecimal amount;
    private String reference;
    private String currency;
}
