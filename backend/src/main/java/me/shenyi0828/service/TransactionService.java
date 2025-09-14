package me.shenyi0828.service;

import me.shenyi0828.common.PageResponse;
import me.shenyi0828.model.TransactionDTO;
import me.shenyi0828.model.TransactionEditRequest;

/**
 * Transaction service interface for business logic operations
 * 
 */
public interface TransactionService {

    /**
     * Create a new transaction
     * 
     * @param transactionEditRequest the transaction request data
     * @return the created transaction with ID
     */
    TransactionDTO createTransaction(TransactionEditRequest transactionEditRequest);

    /**
     * Update an existing transaction
     * 
     * @param transactionId the transaction ID
     * @param transactionEditRequest the updated transaction data
     * @return the updated transaction
     */
    TransactionDTO updateTransaction(String transactionId, TransactionEditRequest transactionEditRequest);



    /**
     * Delete a transaction by ID
     * 
     * @param transactionId the transaction ID
     * @return true if deleted successfully, false otherwise
     */
    boolean deleteTransaction(String transactionId);

    /**
     * Get all transactions with pagination
     * 
     * @param page the page number (0-based)
     * @param size the page size
     * @return paginated transaction list
     */
    PageResponse<TransactionDTO> getAllTransactions(Integer page, Integer size);

    /**
     * Get transaction by ID
     * 
     * @param transactionId the transaction ID
     * @return the transaction DTO
     */
    TransactionDTO getTransactionById(String transactionId);

    /**
     * Check if a transaction exists by transaction ID
     * 
     * @param transactionId the transaction ID
     * @return true if exists, false otherwise
     */
    boolean existsByTransactionId(String transactionId);
}