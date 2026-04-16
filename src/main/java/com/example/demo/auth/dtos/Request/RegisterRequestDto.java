package com.example.demo.auth.dtos.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

    public record RegisterRequestDto(
	
		@NotBlank(message = "El usuario es obligatorio")
		@Size(min = 3, max = 64, message = "El usuario debe tener entre 3 y 64 caracteres")
		@Pattern(
					regexp = "^[a-zA-Z0-9_.]+$",
					message = "El usuario solo puede contener letras, números, guion bajo y punto"
				)
		String username,

		@NotBlank(message = "La contraseña es obligatoria")
		@Size(min = 8, max = 128, message = "La contraseña debe tener entre 8 y 128 caracteres")
		String password) {
}

