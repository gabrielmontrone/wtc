package com.wtc.message.dto;

import com.wtc.message.MessageTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SendMessageRequest(
        @NotNull
        MessageTargetType targetType,

        @NotBlank
        @Size(min = 2, max = 120)
        String subject,

        @NotBlank
        @Size(min = 1, max = 2000)
        String content,

        String customerId,
        String segmentId,
        String groupName,

        List<String> customerIds,

        @NotBlank // Toda mensagem precisa de uma conversa
        String conversationId


) {}