package com.wtc.conversation;

import com.wtc.conversation.dto.ConversationResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListConversationService {

    private final ConversationRepository repository;

    public ListConversationService(ConversationRepository repository) {
        this.repository = repository;
    }

    public List<ConversationResponse> execute(String customerId) {
        return repository.findByCustomerIdOrderByUpdatedAtDesc(customerId)
                .stream()
                .map(conv -> new ConversationResponse(
                        conv.getId(),
                        conv.getCustomerId(),
                        conv.getOperatorId(),
                        conv.getStatus(),
                        conv.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }
}