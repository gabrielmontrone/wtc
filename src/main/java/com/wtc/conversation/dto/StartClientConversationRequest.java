package com.wtc.conversation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Operator request to open (or reuse) a conversation with a client account by email. */
public record StartClientConversationRequest(
        @NotBlank @Email String email
) {}
