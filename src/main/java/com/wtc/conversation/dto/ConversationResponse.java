package com.wtc.conversation.dto;

import java.time.Instant;

public record ConversationResponse(
        String id,
        String customerId,
        String operatorId,
        String status,
        Instant updatedAt
) {}