package com.wtc.segment.dto;

public record SegmentResponse(
    String id,
    String name,
    Boolean vip,
    Boolean active,
    Integer minScore,
    Integer minLoyalty
) {}