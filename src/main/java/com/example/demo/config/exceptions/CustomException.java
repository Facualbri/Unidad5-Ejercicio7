package com.example.demo.config.exceptions;

import org.springframework.http.HttpStatus;
import java.util.List;
import lombok.Getter;

// 👉 Clase base para todas las excepciones personalizada
@Getter
public abstract class CustomException extends RuntimeException {
    
    private final HttpStatus status;
    private final List<String> errors;

    public CustomException(String message, HttpStatus status, List<String> errors) {
        super(message);
        this.status = status;
        this.errors = (errors == null || errors.isEmpty()) ? List.of(message) : List.copyOf(errors);
    }

}