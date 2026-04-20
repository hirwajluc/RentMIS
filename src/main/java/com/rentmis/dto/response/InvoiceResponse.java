package com.rentmis.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private String ebmInvoiceNumber;
    private LocalDateTime issuedAt;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String currency;
    private String ebmVerificationCode;
    private String qrCode;
    private String pdfUrl;
    private String ebmStatus;
    private String verificationUrl;
    private LocalDateTime createdAt;
}
