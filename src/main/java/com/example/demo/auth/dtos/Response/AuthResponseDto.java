package com.example.demo.auth.dtos.Response;

// 👉 DTO = lo que devolvemos al frontend
public record AuthResponseDto(

    String accessToken, // 🔐 token corto (para requests)
    String refreshToken, // 🔄 token largo (para renovar)
    String tokenType, // 👉 "Bearer"
    long accessTokenExpiresIn, // ⏳ duración access
    long refreshTokenExpiresIn // ⏳ duración refresh

) {}
