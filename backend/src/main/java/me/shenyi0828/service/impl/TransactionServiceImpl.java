package me.shenyi0828.service.impl;


import lombok.extern.slf4j.Slf4j;
import me.shenyi0828.common.PageResponse;
import me.shenyi0828.exception.BusinessException;
import me.shenyi0828.exception.ErrorCode;
import me.shenyi0828.mapper.TransactionBeanMapper;
import me.shenyi0828.mapper.TransactionDataMapper;
import me.shenyi0828.model.TransactionDTO;
import me.shenyi0828.model.TransactionEditRequest;
import me.shenyi0828.model.TransactionPO;
import me.shenyi0828.service.CacheInvalidationCallback;
import me.shenyi0828.service.CacheService;
import me.shenyi0828.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 核心实现
 * @author Yi
 */
@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionDataMapper transactionDataMapper;
    private final CacheService cacheService;
    
    private static final AtomicLong counter = new AtomicLong(1);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    public TransactionServiceImpl(TransactionDataMapper transactionDataMapper, CacheService cacheService) {
        this.transactionDataMapper = transactionDataMapper;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional
    public TransactionDTO createTransaction(TransactionEditRequest transactionEditRequest) {
        log.info("Creating new transaction for account: {}", transactionEditRequest.getAccountNumber());
        
        // 预处理 transactionId
        String transactionId = transactionEditRequest.getTransactionId();
        if (transactionId == null || transactionId.trim().isEmpty()) {
            transactionId = generateTransactionId();
        }
        
        // 如题意：防止重复
        if (transactionDataMapper.existsByTransactionId(transactionId)) {
            log.warn("Transaction ID already exists: {}", transactionId);
            throw new BusinessException(ErrorCode.TRANSACTION_ALREADY_EXISTS);
        }
        
        TransactionPO transactionPO = TransactionBeanMapper.INSTANCE.requestToPo(transactionEditRequest);
        transactionPO.setTransactionId(transactionId);
        int result = transactionDataMapper.insert(transactionPO);
        
        if (result > 0) {
            log.info("Transaction created successfully with ID: {}", transactionPO.getId());
            
            // 确保在事务完成后执行
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(
                    new CacheInvalidationCallback(cacheService, transactionId, true)
                );
            }

            TransactionPO savedTransaction = transactionDataMapper.findByTransactionId(transactionId);
            return TransactionBeanMapper.INSTANCE.poToDto(savedTransaction);
        } else {
            log.error("Failed to create transaction for account: {}", transactionEditRequest.getAccountNumber());
            throw new BusinessException(ErrorCode.TRANSACTION_CREATE_FAILED);
        }
    }

    @Override
    @Transactional
    public TransactionDTO updateTransaction(String transactionId, TransactionEditRequest transactionEditRequest) {
        log.info("Updating transaction with ID: {}", transactionId);
        
        TransactionPO existingTransaction = transactionDataMapper.findByTransactionId(transactionId);
        if (existingTransaction == null) {
            log.warn("Transaction not found with ID: {}", transactionId);
            throw new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND);
        }

        TransactionBeanMapper.INSTANCE.updatePoFromRequest(transactionEditRequest, existingTransaction);
        existingTransaction.setTransactionId(transactionId);
        
        int result = transactionDataMapper.update(existingTransaction);
        
        if (result > 0) {
            log.info("Transaction updated successfully with ID: {}", transactionId);

            // 确保在事务完成后执行
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(
                    new CacheInvalidationCallback(cacheService, transactionId, false)
                );
            }
            
            return TransactionBeanMapper.INSTANCE.poToDto(existingTransaction);
        } else {
            log.error("Failed to update transaction with ID: {}", transactionId);
            throw new BusinessException(ErrorCode.TRANSACTION_UPDATE_FAILED);
        }
    }

    @Override
    @Transactional
    public boolean deleteTransaction(String transactionId) {
        log.info("Deleting transaction with ID: {}", transactionId);
        
        TransactionPO existingTransaction = transactionDataMapper.findByTransactionId(transactionId);
        if (existingTransaction == null) {
            log.warn("Transaction not found with ID: {}", transactionId);
            throw new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND);
        }
        
        int result = transactionDataMapper.deleteByTransactionId(transactionId);
        
        if (result > 0) {
            log.info("Transaction deleted successfully with ID: {}", transactionId);

            // 确保在事务完成后执行
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(
                    new CacheInvalidationCallback(cacheService, transactionId, true)
                );
            }
            
            return true;
        } else {
            log.error("Failed to delete transaction with ID: {}", transactionId);
            return false;
        }
    }
    
    @Override
    public PageResponse<TransactionDTO> getAllTransactions(Integer page, Integer size) {
        log.debug("Getting all transactions - page: {}, size: {}", page, size);
        
        int offset = page * size;
        
        List<TransactionPO> transactionPOs = transactionDataMapper.findAllWithPagination(offset, size);

        Long totalCount = cacheService.getTotalCount();
        if (totalCount == null) {
            totalCount = transactionDataMapper.countTotal();
            cacheService.setTotalCount(totalCount);
            log.debug("Total count cached: {}", totalCount);
        }
        
        List<TransactionDTO> transactionDTOs = TransactionBeanMapper.INSTANCE.poListToDtoList(transactionPOs);
        
        return PageResponse.of(transactionDTOs, totalCount, page, size);
    }

    @Override
    public TransactionDTO getTransactionById(String transactionId) {
        log.debug("Getting transaction by ID: {}", transactionId);

        TransactionDTO cachedTransaction = cacheService.getTransaction(transactionId);
        if (cachedTransaction != null) {
            log.debug("Transaction found in cache: {}", transactionId);
            return cachedTransaction;
        }

        TransactionPO transactionPO = transactionDataMapper.findByTransactionId(transactionId);
        if (transactionPO == null) {
            log.warn("Transaction not found with ID: {}", transactionId);
            throw new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND);
        }
        
        TransactionDTO transactionDTO = TransactionBeanMapper.INSTANCE.poToDto(transactionPO);

        cacheService.setTransaction(transactionId, transactionDTO);
        log.debug("Transaction cached: {}", transactionId);
        
        return transactionDTO;
    }

    @Override
    public boolean existsByTransactionId(String transactionId) {
        log.debug("Checking if transaction exists with ID: {}", transactionId);
        
        return transactionDataMapper.existsByTransactionId(transactionId);
    }
    
    /**
     * 唯一值生成
     * 格式: TXN + YYYYMMDD + 4位 SEQ
     */
    private String generateTransactionId() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        long sequence = counter.getAndIncrement();
        return String.format("TXN%s%04d", dateStr, sequence % 10000);
    }
}