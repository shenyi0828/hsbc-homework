package me.shenyi0828.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.shenyi0828.validation.ValidationGroups;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 新增or修改
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEditRequest {
    
    /**
     * Transaction ID (新增选填，更新必填)
     */
    @NotBlank(groups = ValidationGroups.Update.class, message = "Transaction ID cannot be blank for updates")
    private String transactionId;
    
    /**
     * 单位：分
     */
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Integer amount;
    
    /**
     * 类型: 1=EXPENSE, 2=INCOME
     */
    @NotNull(message = "Transaction type cannot be null")
    @Min(value = 1, message = "Transaction type must be 1 (EXPENSE) or 2 (INCOME)")
    @Max(value = 2, message = "Transaction type must be 1 (EXPENSE) or 2 (INCOME)")
    private Byte transactionType;
    
    /**
     * 我的账户号（新增必须，更新可选）
     */
    @NotBlank(groups = ValidationGroups.Create.class, message = "Transaction ID cannot be blank for updates")
    @Size(max = 32, message = "Account number cannot exceed 32 characters")
    private String accountNumber;
    
    /**
     * 对方账户号（新增必须，更新可选）
     */
    @NotBlank(groups = ValidationGroups.Create.class, message = "Transaction ID cannot be blank for updates")
    @Size(max = 32, message = "Counterparty account cannot exceed 32 characters")
    private String counterpartyAccount;
    
    /**
     * 描述，随便填
     */
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}