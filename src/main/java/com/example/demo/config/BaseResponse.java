package com.example.demo.config;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// 👉 Clase genérica para respuestas exitosas
public class BaseResponse<T> {
    private final T data;
    private final String message;
    private final List<String> errors;
    private final String timestamp;

// 👉 Método de fábrica para crear respuestas exitosas
    public static <T> BaseResponse<T> ok(T data, String message) {
        return BaseResponse.<T>builder()
                .data(data)
                .message(message)
                .errors(null)
                .timestamp(getCurrentTimestamp())
                .build();
    }
// 👉 Genera un timestamp en formato ISO 8601 UTC
    private static String getCurrentTimestamp() {
        return DateTimeFormatter.ISO_INSTANT
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());
    }
}