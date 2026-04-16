package com.example.demo.auth.services.impl;

import com.example.demo.auth.models.UserEntity;
import com.example.demo.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service // 👉 Spring lo detecta automáticamente
@RequiredArgsConstructor // 👉 inyección automática
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // 🔐 ESTE MÉTODO ES CLAVE
    // 👉 Spring lo usa en el login automáticamente
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 🔍 1. Buscar usuario en base de datos
        UserEntity user = userRepository.findByUsername(username)

                // ❌ si no existe → error
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado")
                );

        // 🔐 2. Convertir UserEntity → UserDetails (formato de Spring)
        return User.builder()

                // 👤 username
                .username(user.getUsername())

                // 🔐 password (ya encriptada en BD)
                .password(user.getPassword())

                // 🎭 roles (Spring necesita esto)
                .roles(user.getRole().name().replace("ROLE_", ""))

                .build();
    }
}
