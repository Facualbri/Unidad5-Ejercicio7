package com.example.demo.auth.services.interfaces;

import com.example.demo.auth.dtos.Request.LoginRequestDto;
import com.example.demo.auth.dtos.Request.RegisterRequestDto;
import com.example.demo.auth.dtos.Response.AuthResponseDto;

public interface IAuthService {

    // 🔐 REGISTRO DE USUARIO
    void register(RegisterRequestDto request);

    // 🔐 LOGIN (devuelve access + refresh token)
    AuthResponseDto login(LoginRequestDto request);

    // 🔄 REFRESH TOKEN (recibe refresh token y devuelve nuevo access)
    AuthResponseDto refresh(String refreshToken);
}
