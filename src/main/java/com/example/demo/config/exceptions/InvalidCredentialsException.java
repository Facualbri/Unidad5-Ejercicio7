package com.example.demo.config.exceptions;

import java.util.List;
import org.springframework.http.HttpStatus;

// 👉 Se usa cuando login o token son inválidos (401)
public class InvalidCredentialsException extends CustomException {

    public InvalidCredentialsException() {
        super("Credenciales inválidas", HttpStatus.UNAUTHORIZED, List.of("Credenciales inválidas"));
    }
}
