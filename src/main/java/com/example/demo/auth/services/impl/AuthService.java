package com.example.demo.auth.services.impl;

import com.example.demo.auth.dtos.Request.LoginRequestDto;
import com.example.demo.auth.dtos.Request.RegisterRequestDto;
import com.example.demo.auth.dtos.Response.AuthResponseDto;
import com.example.demo.auth.jwt.JwtService;
import com.example.demo.auth.jwt.JwtProperties;
import com.example.demo.auth.models.UserEntity;
import com.example.demo.auth.models.UserRole;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.auth.services.interfaces.IAuthService;
import com.example.demo.config.exceptions.InvalidCredentialsException;
import com.example.demo.config.exceptions.UserAlreadyExistsException;

import lombok.AllArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service // 👉 Marca esta clase como lógica de negocio (Spring la maneja)
@AllArgsConstructor // 👉 Inyección automática de dependencias
public class AuthService implements IAuthService {

    // 🔗 DEPENDENCIAS (todo lo que usa este servicio)
    private final UserRepository userRepository; // 👉 acceso a base de datos
    private final PasswordEncoder passwordEncoder; // 👉 encripta contraseñas
    private final AuthenticationManager authenticationManager; // 👉 valida login
    private final JwtService jwtService; // 👉 genera y valida tokens
    private final JwtProperties jwtProperties; // 👉 tiempos de expiración

    // =========================================================
    // 🔐 REGISTER (crear usuario nuevo)
    // =========================================================
    @Transactional // 👉 si algo falla, revierte todo (seguridad BD)
    @Override
    public void register(RegisterRequestDto request) {

        // 🔍 1. Verificamos si el username ya existe en la BD
        if (userRepository.existsByUsername(request.username())) {

            // ❌ Si existe → lanzamos excepción (la maneja el GlobalExceptionHandler)
            throw new UserAlreadyExistsException();
        }

        // 🔧 2. Creamos el usuario (todavía en memoria)
        UserEntity user = UserEntity.builder()

                // 👤 username que viene del request
                .username(request.username())

                // 🔐 contraseña encriptada (NUNCA guardar en texto plano)
                .password(passwordEncoder.encode(request.password()))

                // 🎭 rol por defecto
                .role(UserRole.ROLE_USER)

                .build();

        // 💾 3. Guardamos el usuario en la base de datos
        userRepository.save(user);

        // ✔ listo, no devolvemos nada (void)
    }

    // =========================================================
    // 🔐 LOGIN (autenticación + generación de tokens)
    // =========================================================
    @Override
    public AuthResponseDto login(LoginRequestDto request) {

        try {

            // 🔍 1. Spring Security valida usuario y contraseña
            // 👉 esto usa UserDetailsService + PasswordEncoder internamente
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(), // 👤 usuario ingresado
                            request.password()  // 🔐 contraseña ingresada
                    )
            );

            // 👤 2. Obtenemos el usuario autenticado
            UserDetails principal = (UserDetails) authentication.getPrincipal();

            // 🎭 3. Obtenemos roles del usuario (ej: ROLE_USER)
            var roles = principal.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // 🔐 4. Generamos ACCESS TOKEN (corto)
            String accessToken = jwtService.generateAccessToken(
                    principal.getUsername(), // 👤 username
                    roles                    // 🎭 roles
            );

            // 🔄 5. Generamos REFRESH TOKEN (largo)
            String refreshToken = jwtService.generateRefreshToken(
                    principal.getUsername()
            );

            // 📤 6. Devolvemos respuesta completa
            return new AuthResponseDto(
                    accessToken,
                    refreshToken,
                    "Bearer", // 👉 tipo de token HTTP estándar
                    jwtProperties.accessExpirationMs(), // ⏳ duración access
                    jwtProperties.refreshExpirationMs() // ⏳ duración refresh
            );

        } catch (BadCredentialsException e) {

            // ❌ Si usuario/contraseña incorrectos
            throw new InvalidCredentialsException();
        }
    }

    // =========================================================
    // 🔄 REFRESH (renovar access token)
    // =========================================================
    @Override
    public AuthResponseDto refresh(String refreshToken) {

        // 🔍 1. Extraemos el username desde el refresh token
        String username = jwtService.extractUsername(refreshToken)

                // ❌ si el token es inválido → excepción
                .orElseThrow(() -> new InvalidCredentialsException());

        // 🔍 2. Buscamos el usuario en la base de datos
        UserEntity user = userRepository.findByUsername(username)

                // ❌ si no existe → excepción
                .orElseThrow(() -> new InvalidCredentialsException());

        // ✔ 3. Validamos que el token:
        // - sea válido
        // - no esté vencido
        // - pertenezca al usuario
        if (!jwtService.isTokenValid(refreshToken, username)) {
            throw new InvalidCredentialsException();
        }

        // 🎭 4. Obtenemos rol del usuario
        var roles = List.of(user.getRole().name());

        // 🔐 5. Generamos NUEVO access token
        String newAccessToken = jwtService.generateAccessToken(username, roles);

        // 📤 6. Devolvemos:
        // - nuevo access token
        // - mismo refresh token
        return new AuthResponseDto(
                newAccessToken,
                refreshToken,
                "Bearer",
                jwtProperties.accessExpirationMs(),
                jwtProperties.refreshExpirationMs()
        );
    }
}