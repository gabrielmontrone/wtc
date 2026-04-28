package com.wtc.segment.dto;

import java.util.List;

public record SegmentCustomersResponse(
    String segmentId,
    Integer totalCustomers,
    List<String> customerIds
) {}