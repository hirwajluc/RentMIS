package com.rentmis.integration.glspay;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class GLSPayCheckoutResponse {
    private String transactionId;
    private String checkoutUrl;
    private String status;
}
