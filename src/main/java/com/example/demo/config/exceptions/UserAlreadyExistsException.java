package com.example.demo.config.exceptions;

import java.util.List;
import org.springframework.http.HttpStatus;

// 👉 Se usa cuando se intenta registrar un usuario con email ya registrado (409)
public class UserAlreadyExistsException extends CustomException {

    public UserAlreadyExistsException() {
        super("El usuario ya existe", HttpStatus.CONFLICT, List.of("El usuario ya fue registrado"));
    }

}
