package me.shenyi0828.exception;

import lombok.Getter;

/**
 * Business exception for handling application-specific errors
 * Supports custom error codes and messages for better error handling
 * 
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * Error code for categorizing the exception
     */
    private final int code;

    /**
     * Constructor with error code and message
     * 
     * @param code the error code
     * @param message the error message
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Constructor with error code, message and cause
     * 
     * @param code the error code
     * @param message the error message
     * @param cause the cause of the exception
     */
    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Constructor with ErrorCode enum
     * 
     * @param errorCode the error code enum
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * Constructor with ErrorCode enum and custom message
     * 
     * @param errorCode the error code enum
     * @param message the custom error message
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}