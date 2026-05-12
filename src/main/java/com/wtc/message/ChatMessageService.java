package com.wtc.message;

import com.wtc.message.dto.ChatMessageRequest;
import com.wtc.message.dto.MessageResponse;
import com.wtc.conversation.ConversationRepository;
import com.wtc.auth.UserDocument;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class ChatMessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    public ChatMessageService(MessageRepository messageRepository, ConversationRepository conversationRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
    }

    public MessageResponse sendReply(String conversationId, ChatMessageRequest request) {
        // 1. Pegar o usuário logado do Token JWT
        UserDocument currentUser = (UserDocument) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 2. Criar e salvar a mensagem
        MessageDocument message = new MessageDocument();
        message.setConversationId(conversationId);
        message.setContent(request.content());
        message.setSenderId(currentUser.getId());
        message.setSenderRole(currentUser.getRole()); // Aqui sabemos se é CLIENTE ou OPERADOR
        message.setCreatedAt(Instant.now());
        message.setStatus(MessageStatus.SENT);

        MessageDocument savedMessage = messageRepository.save(message);

        // 3. Atualizar a data da conversa (Critério: Atualizar conversa com última interação)
        conversationRepository.findById(conversationId).ifPresent(conv -> {
            conv.setUpdatedAt(Instant.now());
            conversationRepository.save(conv);
        });

        return toResponse(savedMessage);
    }

    private MessageResponse toResponse(MessageDocument doc) {
        return new MessageResponse(
                doc.getId(), null, null, doc.getContent(),
                doc.getSenderId(), null, null, null,
                doc.getStatus(), null, doc.getCreatedAt(), doc.getSenderId(),
                doc.getSenderRole()
        );
    }
}