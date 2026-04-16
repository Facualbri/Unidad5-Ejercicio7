package com.example.demo.auth.repository;

import com.example.demo.auth.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 🔍 buscar usuario
    Optional<UserEntity> findByUsername(String username);

    // ✔ verificar si existe
    boolean existsByUsername(String username);
}
