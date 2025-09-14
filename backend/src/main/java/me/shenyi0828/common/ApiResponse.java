package me.shenyi0828.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * API响应包装类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Boolean success; // 是否成功
    private Integer code; // 状态码
    private String message; // 响应消息
    private T data; // 响应数据
    private LocalDateTime timestamp; // 时间戳

    // 成功响应
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message("Operation successful")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 成功响应（自定义消息）
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 成功响应（无数据）
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 错误响应
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 400错误
    public static <T> ApiResponse<T> badRequest(String message) {
        return error(400, message);
    }

    // 404错误
    public static <T> ApiResponse<T> notFound(String message) {
        return error(404, message);
    }

    // 500错误
    public static <T> ApiResponse<T> internalError(String message) {
        return error(500, message);
    }

    // 通用错误响应
    public static <T> ApiResponse<T> error(String message) {
        return error(500, message);
    }
}