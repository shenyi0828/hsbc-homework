package me.shenyi0828.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;

/**
 * 确保缓存操作在事务 commit 后执行
 * @author Yi
 */
@Slf4j
public class CacheInvalidationCallback implements TransactionSynchronization {

    private final CacheService cacheService;
    private final String transactionId;
    private final boolean invalidateTotalCount;

    public CacheInvalidationCallback(CacheService cacheService, String transactionId, boolean invalidateTotalCount) {
        this.cacheService = cacheService;
        this.transactionId = transactionId;
        this.invalidateTotalCount = invalidateTotalCount;
    }

    @Override
    public void afterCommit() {
        try {
            // 删缓存
            if (transactionId != null) {
                cacheService.deleteTransaction(transactionId);
                log.debug("Cache invalidated for transaction: {}", transactionId);
            }

            if (invalidateTotalCount) {
                cacheService.deleteTotalCount();
                log.debug("Total count cache invalidated");
            }
        } catch (Exception e) {
            log.warn("Failed to invalidate cache after transaction commit: {}", e.getMessage());
        }
    }

    @Override
    public void afterCompletion(int status) {
        if (status == STATUS_ROLLED_BACK) {
            log.debug("Transaction rolled back, no cache invalidation needed");
        }
    }
}