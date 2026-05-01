package com.wtc.message;

import com.wtc.message.dto.MessageResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListMessageService {

    // O 'final' garante que o repositório não mude depois de criado
    private final MessageRepository repository;

    // Este é o construtor que o Spring usa para injetar o banco de dados
    public ListMessageService(MessageRepository repository) {
        this.repository = repository;
    }

    public List<MessageResponse> execute(String conversationId) {
        // Agora o 'repository' não será mais NULL
        return repository.findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(msg -> new MessageResponse(
                        msg.getId(),
                        msg.getTargetType(),
                        msg.getSubject(),
                        msg.getContent(),
                        msg.getCustomerId(),
                        msg.getSegmentId(),
                        msg.getGroupName(),
                        msg.getCustomerIds(),
                        msg.getStatus(),
                        msg.getFailureReason(),
                        msg.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}