package com.wtc.message.dto;

import com.wtc.message.MessageStatus;
import com.wtc.message.MessageTargetType;

import java.time.Instant;
import java.util.List;

public record MessageResponse(
        String id,
        MessageTargetType targetType,
        String subject,
        String content,
        String customerId,
        String segmentId,
        String groupName,
        List<String> customerIds,
        MessageStatus status,
        String failureReason,
        Instant createdAt
) {}