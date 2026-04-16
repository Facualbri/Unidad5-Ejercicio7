package com.example.demo.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service // 👉 Servicio de JWT
@RequiredArgsConstructor // 👉 Inyección automática
public class JwtService {

    // 🔐 JWT con HS256 necesita mínimo 32 bytes
    private static final int MIN_SECRET_BYTES = 32;

    // 📥 Propiedades externas (secret, expiración, etc.)
    private final JwtProperties properties;

    // 🔑 Clave de firma
    private SecretKey signingKey;

    // 🚀 Se ejecuta al iniciar la app
    @PostConstruct
    void initSigningKey() {

        // ✔ Validamos algoritmo
        if (!"HS256".equalsIgnoreCase(properties.algorithm())) {
            throw new IllegalStateException("Solo se permite HS256");
        }

        // 🔧 Convertimos secret a bytes
        byte[] keyBytes = properties.secret().getBytes(StandardCharsets.UTF_8);

        // ✔ Validamos longitud mínima
        if (keyBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException("La clave debe tener al menos 32 bytes");
        }

        // 🔑 Generamos clave
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // 🔐 GENERA ACCESS TOKEN (corto, con roles)
    public String generateAccessToken(String username, Collection<String> roles) {
        return generateToken(username, roles, properties.accessExpirationMs());
    }

    // 🔄 GENERA REFRESH TOKEN (largo, sin roles)
    public String generateRefreshToken(String username) {
        return generateToken(username, List.of(), properties.refreshExpirationMs());
    }

    // 🧱 MÉTODO BASE PARA CREAR TOKENS
    private String generateToken(String username, Collection<String> roles, long expirationMs) {

        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(username) // 👤 usuario
                .claim(JwtClaimNames.ROLES, roles) // 🎭 roles
                .issuedAt(Date.from(now)) // 🕒 creación
                .expiration(Date.from(expiry)) // ⏳ expiración
                .signWith(signingKey, Jwts.SIG.HS256) // 🔐 firma
                .compact();
    }

    // 🔥 MÉTODO CLAVE PARA EL FILTRO
    public Optional<Claims> parseValidClaims(String token) {
        try {
            return Optional.of(parseClaims(token)); // ✔ válido
        } catch (JwtException e) {
            return Optional.empty(); // ❌ inválido
        }
    }

    // 🔍 EXTRAER USERNAME (seguro)
    public Optional<String> extractUsername(String token) {
        return parseValidClaims(token)
                .map(Claims::getSubject);
    }

    // 🔍 EXTRAER USERNAME (estricto)
    public String extractUsernameOrThrow(String token) {
        return parseValidClaims(token)
                .map(Claims::getSubject)
                .orElseThrow(() -> new RuntimeException("Token inválido"));
    }

    // ✔ VALIDAR TOKEN
    public boolean isTokenValid(String token, String username) {
        return parseValidClaims(token)
                .map(claims ->
                        claims.getSubject().equals(username) &&
                        claims.getExpiration().after(new Date())
                )
                .orElse(false);
    }

    // 🔧 PARSEAR TOKEN (núcleo)
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey) // 🔐 valida firma
                .build()
                .parseSignedClaims(token)
                .getPayload(); // 📦 datos
    }
}