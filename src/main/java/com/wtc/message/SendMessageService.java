package com.wtc.message;

import com.wtc.message.dto.MessageResponse;
import com.wtc.message.dto.SendMessageRequest;
import com.wtc.auth.UserDocument; // Importante para o cast
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class SendMessageService {

    private final MessageRepository messageRepository;

    public SendMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public MessageResponse execute(SendMessageRequest request) {
        validateBusinessRules(request);

        // --- CORREÇÃO DA EXTRAÇÃO DO USUÁRIO ---
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId;
        String currentUserRole;

        // Verificamos se o principal é o nosso UserDocument para pegar o ID real do MongoDB
        if (auth.getPrincipal() instanceof UserDocument user) {
            currentUserId = user.getId(); // Pega "6a062..." e não o objeto
            currentUserRole = user.getRole();
        } else {
            // Fallback caso o seu SecurityFilter coloque apenas o email/username no principal
            currentUserId = auth.getName();
            currentUserRole = auth.getAuthorities().stream()
                    .map(r -> r.getAuthority().replace("ROLE_", ""))
                    .findFirst()
                    .orElse("CLIENTE");
        }

        MessageDocument document = new MessageDocument();
        document.setTargetType(request.targetType());
        document.setSubject(request.subject());
        document.setContent(request.content());
        document.setConversationId(request.conversationId());
        document.setCustomerId(request.customerId());
        document.setSegmentId(request.segmentId());
        document.setGroupName(request.groupName());
        document.setCustomerIds(request.customerIds());

        // Status como SENT para aparecer no chat imediatamente
        document.setStatus(MessageStatus.SENT);
        document.setSenderId(currentUserId); // Agora salva a String do ID/Email corretamente
        document.setSenderRole(currentUserRole);

        document.setFailureReason(null);
        document.setCreatedAt(Instant.now());

        MessageDocument saved = messageRepository.save(document);

        return toResponse(saved);
    }

    private void validateBusinessRules(SendMessageRequest request) {
        switch (request.targetType()) {
            case CUSTOMER -> validateCustomerTarget(request);
            case SEGMENT -> validateSegmentTarget(request);
            case GROUP -> validateGroupTarget(request);
            default -> throw new RuntimeException("Tipo de envio inválido."); // Ajustado para Runtime simples
        }
    }

    private void validateCustomerTarget(SendMessageRequest request) {
        if (isBlank(request.customerId())) {
            throw new RuntimeException("customerId é obrigatório para envio individual.");
        }
    }

    private void validateSegmentTarget(SendMessageRequest request) {
        if (isBlank(request.segmentId())) {
            throw new RuntimeException("segmentId é obrigatório para envio por segmento.");
        }
    }

    private void validateGroupTarget(SendMessageRequest request) {
        if (isBlank(request.groupName())) {
            throw new RuntimeException("groupName é obrigatório para envio em grupo.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean hasItems(List<String> customerIds) {
        return customerIds != null && !customerIds.isEmpty();
    }

    private MessageResponse toResponse(MessageDocument document) {
        return new MessageResponse(
                document.getId(),
                document.getTargetType(),
                document.getSubject(),
                document.getContent(),
                document.getCustomerId(),
                document.getSegmentId(),
                document.getGroupName(),
                document.getCustomerIds(),
                document.getStatus(),
                document.getFailureReason(),
                document.getCreatedAt(),
                document.getSenderId(),
                document.getSenderRole(),
                document.getImageUrl(),
                document.getRiskLevel(),
                document.getRiskFlags()
        );
    }
}