package com.wtc.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank @Pattern(regexp = "CLIENTE|OPERADOR") String role
) {}