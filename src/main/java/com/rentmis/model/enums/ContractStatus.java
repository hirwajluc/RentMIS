package com.rentmis.model.enums;

public enum ContractStatus {
    DRAFT,
    PENDING_SIGNATURE,
    ACTIVE,
    EXPIRED,
    TERMINATED,
    RENEWED,
    /** Contract data was modified after signing — all parties must review and re-sign. */
    PENDING_RESIGN
}
