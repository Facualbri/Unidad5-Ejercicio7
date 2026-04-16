package com.example.demo.config.exceptions;

import java.util.List;
import org.springframework.http.HttpStatus;

public class ConflictException extends CustomException {

    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT, List.of(message));
    }

    public ConflictException(String message, List<String> errors) {
        super(message, HttpStatus.CONFLICT, errors);
    }
}
