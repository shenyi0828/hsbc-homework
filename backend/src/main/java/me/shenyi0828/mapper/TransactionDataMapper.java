package me.shenyi0828.mapper;

import me.shenyi0828.model.TransactionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Transaction Data Mapper
 * MyBatis mapper interface for transaction CRUD operations
 */
@Mapper
public interface TransactionDataMapper {

    /**
     * Insert a new transaction
     * @param transaction transaction to insert
     * @return number of affected rows
     */
    int insert(TransactionPO transaction);

    /**
     * Delete transaction by transaction ID
     * @param transactionId transaction ID
     * @return number of affected rows
     */
    int deleteByTransactionId(String transactionId);

    /**
     * Update transaction by transaction ID
     * @param transaction transaction with updated data
     */
    int update(TransactionPO transaction);

    /**
     * Select transaction by transaction ID
     * @param transactionId transaction ID
     * @return transaction or null if not found
     */
    TransactionPO findByTransactionId(String transactionId);

    /**
     * Check if transaction exists by transaction ID
     * @param transactionId transaction ID
     * @return true if exists, false otherwise
     */
    boolean existsByTransactionId(String transactionId);

    /**
     * Find all transactions with pagination
     * @param offset starting position
     * @param limit number of records
     * @return list of transactions
     */
    List<TransactionPO> findAllWithPagination(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * Count total transactions
     * @return total count
     */
    Long countTotal();

    /**
     * Select all transactions
     * @return list of all transactions
     */
    List<TransactionPO> selectAll();


}