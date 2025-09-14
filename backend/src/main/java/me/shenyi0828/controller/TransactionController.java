package me.shenyi0828.controller;

import me.shenyi0828.common.ApiResponse;
import me.shenyi0828.common.PageResponse;
import me.shenyi0828.exception.ErrorCode;
import me.shenyi0828.model.TransactionDTO;
import me.shenyi0828.model.TransactionEditRequest;
import me.shenyi0828.model.TransactionDeleteRequest;
import me.shenyi0828.service.TransactionService;
import me.shenyi0828.validation.ValidationGroups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 交易控制器
 */
@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // 分页查询交易记录
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TransactionDTO>>> getAllTransactions(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        
        PageResponse<TransactionDTO> pageResponse = transactionService.getAllTransactions(page, size);
        return ResponseEntity.ok(ApiResponse.success(pageResponse, "Transactions retrieved successfully"));
    }

    // 根据ID查询交易
    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionById(@PathVariable String transactionId) {
        TransactionDTO transaction = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(ApiResponse.success(transaction, "Transaction retrieved successfully"));
    }

    // 创建新交易
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TransactionDTO>> createTransaction(@Validated(ValidationGroups.Create.class) @RequestBody TransactionEditRequest transactionEditRequest) {
        TransactionDTO createdTransaction = transactionService.createTransaction(transactionEditRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdTransaction, "Transaction created successfully"));
    }

    // 更新交易
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<TransactionDTO>> updateTransaction(@Validated(ValidationGroups.Update.class) @RequestBody TransactionEditRequest updateRequest) {
        TransactionDTO updatedTransaction = transactionService.updateTransaction(updateRequest.getTransactionId(), updateRequest);
        return ResponseEntity.ok(ApiResponse.success(updatedTransaction, "Transaction updated successfully"));
    }

    // 删除交易
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<Boolean>> deleteTransaction(@Valid @RequestBody TransactionDeleteRequest deleteRequest) {
        boolean deleted = transactionService.deleteTransaction(deleteRequest.getTransactionId());
        
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success(null, "Transaction deleted successfully"));
        } else {
            return ResponseEntity.ok(ApiResponse.error(ErrorCode.TRANSACTION_DELETE_FAILED.getCode(), "Failed to delete transaction"));
        }
    }
}