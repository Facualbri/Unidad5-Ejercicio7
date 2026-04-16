package com.example.demo.config.exceptions;

import java.util.List;
import org.springframework.http.HttpStatus;

// 👉 Se usa cuando el cliente envía datos incorrectos (400)
public class BadRequestException extends CustomException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, List.of(message));
    }

    public BadRequestException(String message, List<String> errors) {
        super(message, HttpStatus.BAD_REQUEST, errors);
    }
}