package com.wtc.observation.dto;
import jakarta.validation.constraints.NotBlank;

public record ObservationRequest(
        @NotBlank String content
) {}