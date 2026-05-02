package com.wtc.observation.dto;
import java.time.Instant;

public record ObservationResponse(
        String id,
        String customerId,
        String content,
        String authorEmail,
        Instant createdAt
) {}