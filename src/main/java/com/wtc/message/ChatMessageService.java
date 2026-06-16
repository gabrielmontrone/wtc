package com.wtc.message;

import com.wtc.message.dto.ChatMessageRequest;
import com.wtc.message.dto.MessageResponse;
import com.wtc.conversation.ConversationRepository;
import com.wtc.auth.UserDocument;
import com.wtc.auth.UserRepository;
import com.wtc.notification.FcmService;
import com.wtc.audit.AuditService; // 1. Importante
import com.wtc.compliance.RiskAnalysis;
import com.wtc.compliance.RiskAnalyzer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class ChatMessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;
    private final AuditService auditService; // 2. Adicionado
    private final RiskAnalyzer riskAnalyzer; // DLP / Trust & Safety

    public ChatMessageService(MessageRepository messageRepository,
                              ConversationRepository conversationRepository,
                              UserRepository userRepository,
                              FcmService fcmService,
                              AuditService auditService,
                              RiskAnalyzer riskAnalyzer) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.fcmService = fcmService;
        this.auditService = auditService;
        this.riskAnalyzer = riskAnalyzer;
    }

    public MessageResponse sendReply(String conversationId, ChatMessageRequest request) {
        boolean hasContent = request.content() != null && !request.content().isBlank();
        boolean hasImage = request.imageUrl() != null && !request.imageUrl().isBlank();
        if (!hasContent && !hasImage) {
            throw new IllegalArgumentException("A mensagem precisa de um texto ou de uma imagem.");
        }

        UserDocument currentUser = (UserDocument) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // DLP / Trust & Safety: scan the content for sensitive data and risky links.
        RiskAnalysis risk = riskAnalyzer.analyze(request.content());

        MessageDocument message = new MessageDocument();
        message.setConversationId(conversationId);
        message.setContent(request.content());
        message.setImageUrl(request.imageUrl());
        message.setRiskLevel(risk.level().name());
        message.setRiskFlags(risk.flagTypes());
        message.setSenderId(currentUser.getId());
        message.setSenderRole(currentUser.getRole());
        message.setCreatedAt(Instant.now());
        message.setStatus(MessageStatus.SENT);

        MessageDocument savedMessage = messageRepository.save(message);

        conversationRepository.findById(conversationId).ifPresent(conv -> {
            conv.setUpdatedAt(Instant.now());
            conversationRepository.save(conv);

            String targetUserId = currentUser.getRole().equals("OPERADOR") ? conv.getCustomerId() : conv.getOperatorId();

            if (targetUserId != null) {
                userRepository.findById(targetUserId).ifPresent(targetUser -> {
                    String title = "Nova mensagem de " + currentUser.getRole();
                    String body = (request.content() != null && !request.content().isBlank())
                            ? request.content()
                            : "📷 Foto";
                    fcmService.sendPush(targetUser.getFcmToken(), title, body);
                });
            }
        });

        // 3. Registrando auditoria de envio de mensagem
        auditService.log("SEND_MESSAGE", currentUser.getEmail(), "Mensagem enviada na conversa: " + conversationId);

        // 4. Trilha de compliance: registra mensagens com dados sensíveis / links suspeitos
        if (risk.isSuspicious()) {
            auditService.log("SUSPICIOUS_MESSAGE", currentUser.getEmail(),
                    "Conversa " + conversationId + " — risco " + risk.level().name() + " " + risk.summary());
        }

        return toResponse(savedMessage);
    }

    private MessageResponse toResponse(MessageDocument doc) {
        return new MessageResponse(
                doc.getId(), null, null, doc.getContent(),
                doc.getSenderId(), null, null, null,
                doc.getStatus(), null, doc.getCreatedAt(), doc.getSenderId(),
                doc.getSenderRole(), doc.getImageUrl(), doc.getRiskLevel(), doc.getRiskFlags()
        );
    }
}