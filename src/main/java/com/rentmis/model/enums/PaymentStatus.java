package com.rentmis.model.enums;

public enum PaymentStatus {
    PENDING,
    PROCESSING,
    PENDING_CONFIRMATION,   // manual payment awaiting landlord confirmation
    COMPLETED,
    FAILED,
    REFUNDED,
    CANCELLED
}
