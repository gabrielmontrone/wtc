package com.wtc.segment.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSegmentRequest(
    @NotBlank String name,
    Boolean vip,
    Boolean active,
    Integer minScore,
    Integer minLoyalty
) {}
