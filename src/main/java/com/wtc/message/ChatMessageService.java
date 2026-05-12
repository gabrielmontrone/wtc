package com.wtc.message;

import com.wtc.message.dto.ChatMessageRequest;
import com.wtc.message.dto.MessageResponse;
import com.wtc.conversation.ConversationRepository;
import com.wtc.auth.UserDocument;
import com.wtc.auth.UserRepository;
import com.wtc.notification.FcmService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class ChatMessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository; // Adicionado para buscar o token do destinatário
    private final FcmService fcmService;         // Adicionado para disparar o push

    // Construtor atualizado com as novas dependências
    public ChatMessageService(MessageRepository messageRepository,
                              ConversationRepository conversationRepository,
                              UserRepository userRepository,
                              FcmService fcmService) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.fcmService = fcmService;
    }

    public MessageResponse sendReply(String conversationId, ChatMessageRequest request) {
        // 1. Pegar o usuário logado do Token JWT
        UserDocument currentUser = (UserDocument) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 2. Criar e salvar a mensagem no banco
        MessageDocument message = new MessageDocument();
        message.setConversationId(conversationId);
        message.setContent(request.content());
        message.setSenderId(currentUser.getId());
        message.setSenderRole(currentUser.getRole());
        message.setCreatedAt(Instant.now());
        message.setStatus(MessageStatus.SENT);

        MessageDocument savedMessage = messageRepository.save(message);

        // 3. Atualizar a conversa e DISPARAR O PUSH
        conversationRepository.findById(conversationId).ifPresent(conv -> {
            conv.setUpdatedAt(Instant.now());
            conversationRepository.save(conv);

            // --- LÓGICA DE PUSH NOTIFICATION ---
            // Descobrimos quem deve receber: se quem mandou foi OPERADOR, o alvo é o CLIENTE (e vice-versa)
            String targetUserId = currentUser.getRole().equals("OPERADOR") ? conv.getCustomerId() : conv.getOperatorId();

            if (targetUserId != null) {
                userRepository.findById(targetUserId).ifPresent(targetUser -> {
                    String title = "Nova mensagem de " + currentUser.getRole();
                    // O FcmService vai cuidar do envio (mesmo que seja simulado no log agora)
                    fcmService.sendPush(targetUser.getFcmToken(), title, request.content());
                });
            }
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