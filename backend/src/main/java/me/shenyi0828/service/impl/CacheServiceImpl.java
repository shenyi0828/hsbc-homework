package me.shenyi0828.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.shenyi0828.model.TransactionDTO;
import me.shenyi0828.service.CacheService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 缓存实现
 *
 * @author Yi
 */
@Slf4j
@Service
public class CacheServiceImpl implements CacheService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    // Cache keys
    private static final String TOTAL_COUNT_KEY = "transaction:total:count";
    private static final String TRANSACTION_KEY_PREFIX = "transaction:data:";

    // Cache TTL (Time To Live)
    private static final long TOTAL_COUNT_TTL = 300; // 5 minutes
    private static final long TRANSACTION_TTL = 600; // 10 minutes

    public CacheServiceImpl(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Long getTotalCount() {
        try {
            String value = stringRedisTemplate.opsForValue().get(TOTAL_COUNT_KEY);
            if (value != null) {
                log.debug("Cache hit for total count: {}", value);
                return Long.valueOf(value);
            }
            log.debug("Cache miss for total count");
            return null;
        } catch (Exception e) {
            log.warn("Failed to get total count from cache: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void setTotalCount(Long count) {
        try {
            stringRedisTemplate.opsForValue().set(TOTAL_COUNT_KEY, count.toString(), TOTAL_COUNT_TTL, TimeUnit.SECONDS);
            log.debug("Cached total count: {}", count);
        } catch (Exception e) {
            log.warn("Failed to cache total count: {}", e.getMessage());
        }
    }

    @Override
    public void deleteTotalCount() {
        try {
            stringRedisTemplate.delete(TOTAL_COUNT_KEY);
            log.debug("Deleted total count cache");
        } catch (Exception e) {
            log.warn("Failed to delete total count cache: {}", e.getMessage());
        }
    }

    @Override
    public TransactionDTO getTransaction(String transactionId) {
        try {
            String key = TRANSACTION_KEY_PREFIX + transactionId;
            String value = stringRedisTemplate.opsForValue().get(key);
            if (value != null) {
                log.debug("Cache hit for transaction: {}", transactionId);
                return objectMapper.readValue(value, TransactionDTO.class);
            }
            log.debug("Cache miss for transaction: {}", transactionId);
            return null;
        } catch (JsonProcessingException e) {
            log.warn("Failed to deserialize transaction from cache: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Failed to get transaction from cache: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void setTransaction(String transactionId, TransactionDTO transaction) {
        try {
            String key = TRANSACTION_KEY_PREFIX + transactionId;
            String value = objectMapper.writeValueAsString(transaction);
            stringRedisTemplate.opsForValue().set(key, value, TRANSACTION_TTL, TimeUnit.SECONDS);
            log.debug("Cached transaction: {}", transactionId);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize transaction for cache: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Failed to cache transaction: {}", e.getMessage());
        }
    }

    @Override
    public void deleteTransaction(String transactionId) {
        try {
            String key = TRANSACTION_KEY_PREFIX + transactionId;
            stringRedisTemplate.delete(key);
            log.debug("Deleted transaction cache: {}", transactionId);
        } catch (Exception e) {
            log.warn("Failed to delete transaction cache: {}", e.getMessage());
        }
    }

}