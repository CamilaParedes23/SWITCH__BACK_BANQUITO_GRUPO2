package com.banquito.switchpagos.common.response;

public record ApiResponse<T>(
        Boolean exitoso,
        String mensaje,
        T datos) {

    public static <T> ApiResponse<T> ok(String mensaje, T datos) {
        return new ApiResponse<>(Boolean.TRUE, mensaje, datos);
    }

    public static <T> ApiResponse<T> error(String mensaje, T datos) {
        return new ApiResponse<>(Boolean.FALSE, mensaje, datos);
    }
}
