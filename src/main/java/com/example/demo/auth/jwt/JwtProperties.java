package com.example.demo.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

// 👉 Esta clase lee datos desde application.properties
// prefix = "app.jwt" → todas las propiedades empiezan con eso
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(

    String secret, // 🔐 clave secreta para firmar JWT
    String algorithm, // 🔐 algoritmo (HS256)
    long accessExpirationMs, // ⏳ duración del access token
    long refreshExpirationMs // ⏳ duración del refresh token

) {}

