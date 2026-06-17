package com.wtc.conversation;

import com.wtc.auth.AccessControlService;
import com.wtc.auth.UserDocument;
import com.wtc.auth.UserRepository;
import com.wtc.conversation.dto.ConversationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

/**
 * Links the current operator to a client account (looked up by email) in a shared conversation,
 * so the operator can message the client and the client sees it in their own conversation. Reuses
 * an existing operator/client thread when present, claims the client's unassigned conversation,
 * or creates a new one.
 */
@Service
public class StartClientConversationService {

    private static final String ROLE_OPERATOR = "OPERADOR";

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final AccessControlService accessControl;

    public StartClientConversationService(ConversationRepository conversationRepository,
                                          UserRepository userRepository,
                                          AccessControlService accessControl) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.accessControl = accessControl;
    }

    public ConversationResponse startWithClient(String email) {
        if (!accessControl.isOperator()) {
            throw new AccessDeniedException("Apenas operadores podem iniciar conversas com clientes.");
        }
        UserDocument operator = accessControl.currentUser();

        UserDocument client = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Nenhuma conta encontrada para esse e-mail."));
        if (ROLE_OPERATOR.equalsIgnoreCase(client.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O destinatário precisa ser uma conta de cliente.");
        }

        String clientId = client.getId();
        List<ConversationDocument> existing = conversationRepository.findByCustomerIdOrderByUpdatedAtDesc(clientId);

        ConversationDocument conversation = existing.stream()
                .filter(c -> operator.getId().equals(c.getOperatorId()))
                .findFirst()
                .orElseGet(() -> claimOrCreate(existing, clientId, operator.getId()));

        return toResponse(conversation);
    }

    private ConversationDocument claimOrCreate(List<ConversationDocument> existing, String clientId, String operatorId) {
        ConversationDocument conversation = existing.stream()
                .filter(c -> c.getOperatorId() == null)
                .findFirst()
                .orElseGet(ConversationDocument::new);
        conversation.setCustomerId(clientId);
        conversation.setOperatorId(operatorId);
        if (conversation.getStatus() == null) {
            conversation.setStatus("OPEN");
        }
        conversation.setUpdatedAt(Instant.now());
        return conversationRepository.save(conversation);
    }

    private ConversationResponse toResponse(ConversationDocument c) {
        return new ConversationResponse(c.getId(), c.getCustomerId(), c.getOperatorId(), c.getStatus(), c.getUpdatedAt());
    }
}
