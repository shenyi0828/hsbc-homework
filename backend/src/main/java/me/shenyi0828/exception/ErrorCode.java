package me.shenyi0828.exception;

/**
 * 业务异常错误码枚举
 */
public enum ErrorCode {

    // 系统错误
    SYSTEM_ERROR(-1, "System error occurred"),
    // 参数类型错误
    INVALID_PARAMETER(-2, "Invalid parameter"),
    
    // 交易记录未找到
    TRANSACTION_NOT_FOUND(-101, "Transaction not found"),
    // 交易记录已存在
    TRANSACTION_ALREADY_EXISTS(-102, "Transaction already exists"),
    // 创建交易失败
    TRANSACTION_CREATE_FAILED(-103, "Failed to create transaction"),
    // 更新交易失败
    TRANSACTION_UPDATE_FAILED(-104, "Failed to update transaction"),
    // 删除交易失败
    TRANSACTION_DELETE_FAILED(-105, "Failed to delete transaction"),
    ;

    private final int code;
    private final String message;

    // 构造函数
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    // 获取错误信息
    public String getMessage() {
        return message;
    }
}