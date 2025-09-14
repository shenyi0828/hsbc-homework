package me.shenyi0828.exception;

import lombok.extern.slf4j.Slf4j;
import me.shenyi0828.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessErrors(BusinessException ex) {
        log.warn("Business error: {}", ex.getMessage());
        
        return ResponseEntity.ok(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    /**
     * 处理请求体验证错误
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        
        String errorMessage = "Validation failed: " + String.join(", ", errors);
        return ResponseEntity.ok(ApiResponse.error(ErrorCode.INVALID_PARAMETER.getCode(), errorMessage));
    }

    /**
     * 处理表单数据验证错误
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBindErrors(BindException ex) {
        log.warn("Bind error: {}", ex.getMessage());
        
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        
        String errorMessage = "Validation failed: " + String.join(", ", errors);
        return ResponseEntity.ok(ApiResponse.error(ErrorCode.INVALID_PARAMETER.getCode(), errorMessage));
    }

    /**
     * 处理约束违反错误
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationErrors(ConstraintViolationException ex) {
        log.warn("Constraint violation error: {}", ex.getMessage());
        
        List<String> errors = new ArrayList<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }
        
        String errorMessage = "Validation failed: " + String.join(", ", errors);
        return ResponseEntity.ok(ApiResponse.error(ErrorCode.INVALID_PARAMETER.getCode(), errorMessage));
    }

    /**
     * 处理参数类型不匹配错误
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchErrors(MethodArgumentTypeMismatchException ex) {
        log.warn("Type mismatch error: {}", ex.getMessage());
        
        String errorMessage = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        
        return ResponseEntity.ok(ApiResponse.error(ErrorCode.INVALID_PARAMETER.getCode(), errorMessage));
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentErrors(IllegalArgumentException ex) {
        log.warn("Illegal argument error: {}", ex.getMessage());
        
        return ResponseEntity.ok(ApiResponse.error(ErrorCode.INVALID_PARAMETER.getCode(), "Invalid request: " + ex.getMessage()));
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeErrors(RuntimeException ex) {
        log.error("Runtime error: {}", ex.getMessage(), ex);
        
        return ResponseEntity.ok(ApiResponse.error(ErrorCode.SYSTEM_ERROR.getCode(), "Internal server error: " + ex.getMessage()));
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericErrors(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        return ResponseEntity.ok(ApiResponse.error(ErrorCode.SYSTEM_ERROR.getCode(), "An unexpected error occurred. Please try again later."));
    }
}