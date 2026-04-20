package com.rentmis.dto.response;

import com.rentmis.model.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private String referenceNumber;
    private UserResponse tenant;
    private UnitResponse unit;
    private BigDecimal amount;
    private BigDecimal penaltyAmount;
    private BigDecimal totalAmount;
    private String currency;
    private PaymentStatus status;
    private String paymentMethod;
    private Integer paymentPeriodMonth;
    private Integer paymentPeriodYear;
    private LocalDate dueDate;
    private LocalDateTime paidAt;
    private String glspayTransactionId;
    private String glspayCheckoutUrl;
    private InvoiceResponse invoice;
    private String notes;
    private LocalDateTime createdAt;

    // Manual payment fields
    private String receiptFilePath;
    private String receiptOriginalName;
    private String confirmedByName;
    private LocalDateTime confirmedAt;
    private String actionNote;
}
