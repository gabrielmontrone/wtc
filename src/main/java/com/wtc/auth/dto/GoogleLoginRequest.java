package com.wtc.auth.dto;

import jakarta.validation.constraints.NotBlank;

/** Payload for Google Sign-In: the ID token obtained by the Android client. */
public record GoogleLoginRequest(
        @NotBlank String idToken
) {}
