package com.rentmis.integration.ebm;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class EBMInvoiceResponse {
    private String ebmInvoiceNumber;
    private String ebmVerificationCode;
    private String qrCode;
    private String verificationUrl;
    private String status; // SUCCESS, FAILED
    private String errorMessage;
    private String rawResponse;
}
