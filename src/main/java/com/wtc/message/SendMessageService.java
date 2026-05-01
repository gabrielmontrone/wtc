package com.wtc.message;

import com.wtc.message.dto.MessageResponse;
import com.wtc.message.dto.SendMessageRequest;
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

        MessageDocument document = new MessageDocument();
        document.setTargetType(request.targetType());
        document.setSubject(request.subject());
        document.setContent(request.content());
        document.setConversationId(request.conversationId());
        document.setCustomerId(request.customerId());
        document.setSegmentId(request.segmentId());
        document.setGroupName(request.groupName());
        document.setCustomerIds(request.customerIds());
        document.setStatus(MessageStatus.PENDING);
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
            default -> throw new MessageDispatchException("Tipo de envio inválido.");
        }
    }

    private void validateCustomerTarget(SendMessageRequest request) {
        if (isBlank(request.customerId())) {
            throw new MessageDispatchException("customerId é obrigatório para envio individual.");
        }

        if (!isBlank(request.segmentId()) || !isBlank(request.groupName()) || hasItems(request.customerIds())) {
            throw new MessageDispatchException("Para envio individual, informe apenas customerId.");
        }
    }

    private void validateSegmentTarget(SendMessageRequest request) {
        if (isBlank(request.segmentId())) {
            throw new MessageDispatchException("segmentId é obrigatório para envio por segmento.");
        }

        if (!isBlank(request.customerId()) || !isBlank(request.groupName()) || hasItems(request.customerIds())) {
            throw new MessageDispatchException("Para envio por segmento, informe apenas segmentId.");
        }
    }

    private void validateGroupTarget(SendMessageRequest request) {
        if (isBlank(request.groupName())) {
            throw new MessageDispatchException("groupName é obrigatório para envio em grupo.");
        }

        if (!hasItems(request.customerIds())) {
            throw new MessageDispatchException("customerIds é obrigatório para criação de grupo.");
        }

        if (!isBlank(request.customerId()) || !isBlank(request.segmentId())) {
            throw new MessageDispatchException("Para envio em grupo, informe apenas groupName e customerIds.");
        }
    }

    private boolean hasItems(List<String> customerIds) {
        return customerIds != null && !customerIds.isEmpty();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
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
                document.getCreatedAt()
        );
    }
}