package com.wtc.conversation.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateConversationRequest(
        @NotBlank(message = "customerId é obrigatório")
        String customerId
) {}
