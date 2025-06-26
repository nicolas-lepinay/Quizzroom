package com.ynov.kiwi.api.response;

public class ResponseUtil {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("success", 200, message, data);
    }
    public static <T> ApiResponse<T> success(String message, int code, T data) {
        return new ApiResponse<>("success", code, message, data);
    }
    public static <T> ApiResponse<T> error(String message, int code) {
        return new ApiResponse<>("error", code, message, null);
    }
    public static <T> ApiResponse<T> error(String message, int code, T data) {
        return new ApiResponse<>("error", code, message, data);
    }
}

