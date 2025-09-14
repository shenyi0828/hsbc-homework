package me.shenyi0828.service;

import me.shenyi0828.model.TransactionDTO;

/**
 * Cache service interface for transaction data
 * 
 */
public interface CacheService {

    /**
     * Get total transaction count from cache
     * 
     * @return total count or null if not cached
     */
    Long getTotalCount();

    /**
     * Set total transaction count to cache
     * 
     * @param count total count
     */
    void setTotalCount(Long count);

    /**
     * Delete total count cache
     */
    void deleteTotalCount();

    /**
     * Get transaction by ID from cache
     * 
     * @param transactionId transaction ID
     * @return transaction DTO or null if not cached
     */
    TransactionDTO getTransaction(String transactionId);

    /**
     * Set transaction to cache
     * 
     * @param transactionId transaction ID
     * @param transaction transaction DTO
     */
    void setTransaction(String transactionId, TransactionDTO transaction);

    /**
     * Delete transaction cache by ID
     * 
     * @param transactionId transaction ID
     */
    void deleteTransaction(String transactionId);

}