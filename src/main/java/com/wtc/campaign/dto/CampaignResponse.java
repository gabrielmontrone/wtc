package com.wtc.campaign.dto;

import java.time.Instant;

public record CampaignResponse(
        String id,
        String name,
        String description,
        String type,
        String content,
        String segmentTargetId,
        String callCode,
        String status,
        Instant createdAt
) {}