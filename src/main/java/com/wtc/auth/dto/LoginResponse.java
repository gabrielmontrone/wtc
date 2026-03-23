package com.wtc.auth.dto;

public record LoginResponse(
        String token,
        String role
) {}