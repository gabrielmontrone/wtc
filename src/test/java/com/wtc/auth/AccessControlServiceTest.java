package com.wtc.auth;

import com.wtc.conversation.ConversationDocument;
import com.wtc.conversation.ConversationRepository;
import com.wtc.customer.CustomerDocument;
import com.wtc.customer.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessControlServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private ConversationRepository conversationRepository;

    private AccessControlService service() {
        return new AccessControlService(customerRepository, conversationRepository);
    }

    private void authenticateAs(String userId, String role) {
        UserDocument user = new UserDocument();
        user.setId(userId);
        user.setRole(role);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of()));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private CustomerDocument customerOwnedBy(String ownerId) {
        CustomerDocument c = new CustomerDocument();
        c.setId("cust1");
        c.setOwnerId(ownerId);
        return c;
    }

    @Test
    void operatorCanAccessOwnCustomer() {
        authenticateAs("op1", "OPERADOR");
        when(customerRepository.findById("cust1")).thenReturn(Optional.of(customerOwnedBy("op1")));

        assertThatCode(() -> service().checkCustomerAccess("cust1")).doesNotThrowAnyException();
    }

    @Test
    void operatorCannotAccessAnotherOperatorsCustomer() {
        authenticateAs("op2", "OPERADOR");
        when(customerRepository.findById("cust1")).thenReturn(Optional.of(customerOwnedBy("op1")));

        assertThatThrownBy(() -> service().checkCustomerAccess("cust1"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void clientCanAccessOnlyOwnProfile() {
        authenticateAs("client1", "CLIENTE");

        assertThatCode(() -> service().checkCustomerAccess("client1")).doesNotThrowAnyException();
        assertThatThrownBy(() -> service().checkCustomerAccess("client2"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void clientCanAccessOnlyOwnConversation() {
        authenticateAs("client1", "CLIENTE");
        ConversationDocument conv = new ConversationDocument();
        conv.setId("conv1");
        conv.setCustomerId("client1");
        when(conversationRepository.findById("conv1")).thenReturn(Optional.of(conv));

        assertThatCode(() -> service().checkConversationAccess("conv1")).doesNotThrowAnyException();
    }

    @Test
    void operatorCannotAccessForeignConversation() {
        authenticateAs("op2", "OPERADOR");
        ConversationDocument conv = new ConversationDocument();
        conv.setId("conv1");
        conv.setCustomerId("cust1");
        conv.setOperatorId("op1");
        when(conversationRepository.findById("conv1")).thenReturn(Optional.of(conv));
        lenient().when(customerRepository.findById("cust1")).thenReturn(Optional.of(customerOwnedBy("op1")));

        assertThatThrownBy(() -> service().checkConversationAccess("conv1"))
                .isInstanceOf(AccessDeniedException.class);
    }
}
