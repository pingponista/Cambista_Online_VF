package com.cambistaonline.order.application.dto;

public class OrderApiResponseDto<T> {
    private boolean success;
    private String message;
    private T data;

    public OrderApiResponseDto() {}

    public OrderApiResponseDto(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> OrderApiResponseDto<T> ok(String message, T data) {
        return new OrderApiResponseDto<>(true, message, data);
    }

    public static <T> OrderApiResponseDto<T> error(String message) {
        return new OrderApiResponseDto<>(false, message, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
