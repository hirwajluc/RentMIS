package com.rentmis.integration.blockchain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class BlockchainRecordResult {
    private String txHash;
    private String network;
    private String contractHash;
    private LocalDateTime timestamp;
    private Long blockNumber;
    private boolean success;
    private String errorMessage;
}
