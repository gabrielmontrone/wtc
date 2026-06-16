package com.wtc.message.dto;

/**
 * Mensagem enviada dentro de uma conversa. Aceita texto, uma imagem (via {@code imageUrl})
 * ou ambos. A obrigatoriedade de ao menos um dos campos é validada no serviço.
 */
public record ChatMessageRequest(
        String content,
        String imageUrl
) {}