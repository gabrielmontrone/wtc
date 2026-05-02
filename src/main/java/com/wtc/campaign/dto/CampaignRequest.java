package com.wtc.campaign.dto;

import jakarta.validation.constraints.NotBlank;

public record CampaignRequest(
        @NotBlank String name,
        String description,
        @NotBlank String type,
        @NotBlank String content,
        String segmentTargetId,
        @NotBlank String callCode
) {}