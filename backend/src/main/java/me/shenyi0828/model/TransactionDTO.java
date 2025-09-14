package me.shenyi0828.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Transaction Data Transfer Object
 * Used for transferring transaction data between layers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    
    /**
     * Transaction ID
     */
    private Long id;
    
    /**
     * Business transaction ID
     */
    private String transactionId;
    
    /**
     * Transaction amount in cents
     */
    private Integer amount;
    
    /**
     * Transaction type: 1=DEBIT, 2=CREDIT
     */
    private Byte transactionType;
    
    /**
     * Account number
     */
    private String accountNumber;
    
    /**
     * Counterparty account number
     */
    private String counterpartyAccount;
    
    /**
     * Transaction description
     */
    private String description;
    
    /**
     * Creation timestamp
     */
    private LocalDateTime createdAt;
    
    /**
     * Last update timestamp
     */
    private LocalDateTime updatedAt;
}