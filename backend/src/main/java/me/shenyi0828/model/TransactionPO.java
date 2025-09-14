package me.shenyi0828.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Transaction Persistent Object
 * Maps to the transactions table in database
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPO {

    private Long id;
    private String transactionId;
    private Integer amount;
    private Byte transactionType;
    private String accountNumber;
    private String counterpartyAccount;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}