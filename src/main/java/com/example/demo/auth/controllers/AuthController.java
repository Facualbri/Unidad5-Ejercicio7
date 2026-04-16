package com.example.demo.auth.controllers;

import com.example.demo.auth.dtos.Request.LoginRequestDto;
import com.example.demo.auth.dtos.Request.RegisterRequestDto;
import com.example.demo.auth.dtos.Request.RefreshRequestDto;
import com.example.demo.auth.dtos.Response.AuthResponseDto;
import com.example.demo.auth.services.interfaces.IAuthService;
import com.example.demo.config.BaseResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // 👉 indica que es un controlador REST (devuelve JSON)
@RequestMapping("/auth") // 👉 todas las rutas empiezan con /auth
@RequiredArgsConstructor // 👉 inyección automática del service
public class AuthController {

    // 🔗 Conectamos con la lógica de negocio
    private final IAuthService authService;

   
    // =========================================================
    // 🔐 REGISTER (crear usuario)
    // =========================================================
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Object>> register(
            @RequestBody RegisterRequestDto request // 👉 datos que vienen del frontend
    ) {

        // 👉 Llamamos al service (lógica real)
        authService.register(request);

        // 📤 Respondemos usando BaseResponse (formato estándar)
        return ResponseEntity.ok(
                BaseResponse.ok(null, "Usuario registrado correctamente")
        );
    }

    // =========================================================
    // 🔐 LOGIN (genera tokens)
    // =========================================================
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponseDto>> login(
            @RequestBody LoginRequestDto request
    ) {

        // 👉 Lógica de autenticación
        AuthResponseDto response = authService.login(request);

        // 📤 devolvemos tokens
        return ResponseEntity.ok(
                BaseResponse.ok(response, "Login exitoso")
        );
    }

    // =========================================================
    // 🔄 REFRESH TOKEN (renueva access token)
    // =========================================================
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<AuthResponseDto>> refresh(
            @RequestBody RefreshRequestDto request
    ) {

        // 👉 usamos el refresh token para generar uno nuevo
        AuthResponseDto response = authService.refresh(request.refreshToken());

        return ResponseEntity.ok(
                BaseResponse.ok(response, "Token renovado correctamente")
        );
    }
}