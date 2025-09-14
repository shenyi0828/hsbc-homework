package me.shenyi0828.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * Transaction Delete Request Object
 * Used for deleting existing transactions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDeleteRequest {
    
    /**
     * Transaction ID (required for deletion)
     */
    @NotBlank(message = "Transaction ID cannot be blank")
    private String transactionId;
}