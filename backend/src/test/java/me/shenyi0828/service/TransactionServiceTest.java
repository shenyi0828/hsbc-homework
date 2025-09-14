package me.shenyi0828.service;

import me.shenyi0828.common.PageResponse;
import me.shenyi0828.exception.BusinessException;
import me.shenyi0828.exception.ErrorCode;
import me.shenyi0828.model.TransactionEditRequest;
import me.shenyi0828.model.TransactionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TransactionService
 * 
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    private TransactionEditRequest transactionEditRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        transactionEditRequest = new TransactionEditRequest();
        transactionEditRequest.setAccountNumber("ACC123456");
        transactionEditRequest.setAmount(10000); // 100.00 in cents
        transactionEditRequest.setTransactionType((byte) 1); // DEBIT
        transactionEditRequest.setCounterpartyAccount("ACC789012");
        transactionEditRequest.setDescription("Test transaction");
    }

    @Test
    void createTransaction_Success() {
        // When
        TransactionDTO result = transactionService.createTransaction(transactionEditRequest);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTransactionId());
        assertEquals("ACC123456", result.getAccountNumber());
        assertEquals(10000, result.getAmount());
        assertEquals((byte) 1, result.getTransactionType());
        assertEquals("ACC789012", result.getCounterpartyAccount());
        assertEquals("Test transaction", result.getDescription());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void createTransaction_WithSpecificTransactionId_Success() {
        // Given
        transactionEditRequest.setTransactionId("CUSTOM001");

        // When
        TransactionDTO result = transactionService.createTransaction(transactionEditRequest);

        // Then
        assertNotNull(result);
        assertEquals("CUSTOM001", result.getTransactionId());
        assertEquals("ACC123456", result.getAccountNumber());
    }

    @Test
    void createTransaction_DuplicateTransactionId_ThrowsException() {
        // Given
        transactionEditRequest.setTransactionId("DUPLICATE001");
        transactionService.createTransaction(transactionEditRequest);

        // When & Then
        TransactionEditRequest duplicateRequest = new TransactionEditRequest();
        duplicateRequest.setTransactionId("DUPLICATE001");
        duplicateRequest.setAccountNumber("ACC999999");
        duplicateRequest.setAmount(5000);
        duplicateRequest.setTransactionType((byte) 2);
        duplicateRequest.setCounterpartyAccount("ACC111111");
        duplicateRequest.setDescription("Duplicate transaction");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
             transactionService.createTransaction(duplicateRequest);
         });
         assertEquals(ErrorCode.TRANSACTION_ALREADY_EXISTS.getCode(), exception.getCode());
    }

    @Test
    void updateTransaction_Success() {
        // Given
        TransactionDTO created = transactionService.createTransaction(transactionEditRequest);
        String transactionId = created.getTransactionId();

        TransactionEditRequest updateRequest = new TransactionEditRequest();
        updateRequest.setAmount(20000); // 200.00 in cents
        updateRequest.setTransactionType((byte) 2); // CREDIT
        updateRequest.setDescription("Updated transaction");

        // When
        TransactionDTO result = transactionService.updateTransaction(transactionId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(transactionId, result.getTransactionId());
        assertEquals(20000, result.getAmount());
        assertEquals((byte) 2, result.getTransactionType());
        assertEquals("Updated transaction", result.getDescription());
    }

    @Test
    void updateTransaction_TransactionNotFound_ThrowsException() {
        // Given
        TransactionEditRequest updateRequest = new TransactionEditRequest();
        updateRequest.setAmount(20000);
        updateRequest.setTransactionType((byte) 2);
        updateRequest.setDescription("Updated transaction");

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.updateTransaction("NONEXISTENT", updateRequest);
        });
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void deleteTransaction_Success() {
        // Given
        TransactionDTO created = transactionService.createTransaction(transactionEditRequest);
        String transactionId = created.getTransactionId();

        // When
        boolean result = transactionService.deleteTransaction(transactionId);

        // Then
        assertTrue(result);
        
        // Verify transaction is deleted
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.getTransactionById(transactionId);
        });
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void deleteTransaction_TransactionNotFound_ThrowsException() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.deleteTransaction("NONEXISTENT");
        });
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void getAllTransactions_Success() {
        // Given
        transactionService.createTransaction(transactionEditRequest);
        
        TransactionEditRequest secondRequest = new TransactionEditRequest();
        secondRequest.setAccountNumber("ACC999999");
        secondRequest.setAmount(5000);
        secondRequest.setTransactionType((byte) 2);
        secondRequest.setCounterpartyAccount("ACC111111");
        secondRequest.setDescription("Second transaction");
        transactionService.createTransaction(secondRequest);

        // When
        PageResponse<TransactionDTO> result = transactionService.getAllTransactions(0, 10);

        // Then
        assertNotNull(result);
        assertTrue(result.getTotalElements() >= 2);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().size() >= 2);
        assertEquals(Integer.valueOf(0), result.getNumber());
        assertEquals(Integer.valueOf(10), result.getSize());
    }

    @Test
    void getTransactionById_Success() {
        // Given
        TransactionDTO created = transactionService.createTransaction(transactionEditRequest);
        String transactionId = created.getTransactionId();

        // When
        TransactionDTO result = transactionService.getTransactionById(transactionId);

        // Then
        assertNotNull(result);
        assertEquals(transactionId, result.getTransactionId());
        assertEquals("ACC123456", result.getAccountNumber());
        assertEquals(10000, result.getAmount());
    }

    @Test
    void getTransactionById_TransactionNotFound_ThrowsException() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.getTransactionById("NONEXISTENT");
        });
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void existsByTransactionId_True() {
        // Given
        TransactionDTO created = transactionService.createTransaction(transactionEditRequest);
        String transactionId = created.getTransactionId();

        // When
        boolean result = transactionService.existsByTransactionId(transactionId);

        // Then
        assertTrue(result);
    }

    @Test
    void existsByTransactionId_False() {
        // When
        boolean result = transactionService.existsByTransactionId("NONEXISTENT");

        // Then
        assertFalse(result);
    }
}