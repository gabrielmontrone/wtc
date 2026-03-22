package com.wtc.message;

import com.wtc.message.dto.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListMessageService {

    @Autowired
    private MessageRepository repository;

    public List<MessageResponse> execute(String customerId) {
        // Busca as mensagens no banco e transforma em DTO para o frontend
        return repository.findByCustomerId(customerId)
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