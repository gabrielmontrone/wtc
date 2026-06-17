package com.wtc.auth;

import com.wtc.conversation.ConversationDocument;
import com.wtc.conversation.ConversationRepository;
import com.wtc.customer.CustomerDocument;
import com.wtc.customer.CustomerRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Central place for per-account data-isolation checks. An OPERADOR may only touch the
 * customers it owns and the conversations it participates in; a CLIENTE may only touch
 * their own profile/conversation. Throws {@link AccessDeniedException} (→ 403) otherwise.
 */
@Service
public class AccessControlService {

    private final CustomerRepository customerRepository;
    private final ConversationRepository conversationRepository;

    public AccessControlService(CustomerRepository customerRepository,
                                ConversationRepository conversationRepository) {
        this.customerRepository = customerRepository;
        this.conversationRepository = conversationRepository;
    }

    public UserDocument currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDocument user)) {
            throw new AccessDeniedException("Usuário não autenticado.");
        }
        return user;
    }

    public boolean isOperator() {
        return "OPERADOR".equalsIgnoreCase(currentUser().getRole());
    }

    /** Ensures the current user may access a customer's data/conversations. */
    public void checkCustomerAccess(String customerId) {
        UserDocument user = currentUser();
        if (isOperator()) {
            CustomerDocument customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new AccessDeniedException("Contato não encontrado."));
            if (!user.getId().equals(customer.getOwnerId())) {
                throw new AccessDeniedException("Contato pertence a outra conta.");
            }
        } else if (!user.getId().equals(customerId)) {
            // Cliente só acessa o próprio perfil (id == userId).
            throw new AccessDeniedException("Acesso negado ao contato.");
        }
    }

    /** Ensures the current user participates in a conversation. */
    public void checkConversationAccess(String conversationId) {
        UserDocument user = currentUser();
        ConversationDocument conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AccessDeniedException("Conversa não encontrada."));
        if (isOperator()) {
            boolean ownsConversation = user.getId().equals(conversation.getOperatorId());
            boolean ownsCustomer = conversation.getCustomerId() != null
                    && customerRepository.findById(conversation.getCustomerId())
                        .map(customer -> user.getId().equals(customer.getOwnerId()))
                        .orElse(false);
            if (!ownsConversation && !ownsCustomer) {
                throw new AccessDeniedException("Conversa pertence a outra conta.");
            }
        } else if (!user.getId().equals(conversation.getCustomerId())) {
            throw new AccessDeniedException("Acesso negado à conversa.");
        }
    }
}
