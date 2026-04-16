package com.example.demo.config.exceptions;

import java.util.List;
import org.springframework.http.HttpStatus;

// 👉 Se usa cuando no se encuentra un recurso (404)
public class NotFoundException extends CustomException {

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, List.of(message));
    }

    public NotFoundException(String message, List<String> errors) {
        super(message, HttpStatus.NOT_FOUND, errors);
    }
}