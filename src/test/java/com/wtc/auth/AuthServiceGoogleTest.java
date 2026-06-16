package com.wtc.auth;

import com.wtc.audit.AuditService;
import com.wtc.auth.dto.LoginResponse;
import com.wtc.conversation.ConversationDocument;
import com.wtc.conversation.ConversationRepository;
import com.wtc.customer.CustomerDocument;
import com.wtc.customer.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceGoogleTest {

    @Mock private UserRepository userRepository;
    @Mock private TokenService tokenService;
    @Mock private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Mock private AuditService auditService;
    @Mock private CustomerRepository customerRepository;
    @Mock private ConversationRepository conversationRepository;
    @Mock private GoogleTokenVerifier googleTokenVerifier;

    private AuthService service() {
        return new AuthService(userRepository, tokenService, passwordEncoder, auditService,
                customerRepository, conversationRepository, googleTokenVerifier);
    }

    @Test
    void createsClienteAndBootstrapsProfileOnFirstGoogleLogin() {
        when(googleTokenVerifier.verify("id-token")).thenReturn(new GoogleUserInfo("novo@gmail.com", "Novo"));
        when(userRepository.findByEmail("novo@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(userRepository.save(any(UserDocument.class))).thenAnswer(invocation -> {
            UserDocument u = invocation.getArgument(0);
            u.setId("user-1");
            return u;
        });
        when(tokenService.generateToken(any(UserDocument.class))).thenReturn("jwt");

        LoginResponse response = service().loginWithGoogle("id-token");

        assertThat(response.token()).isEqualTo("jwt");
        assertThat(response.role()).isEqualTo("CLIENTE");
        verify(userRepository).save(any(UserDocument.class));
        verify(customerRepository).save(any(CustomerDocument.class));
        verify(conversationRepository).save(any(ConversationDocument.class));
    }

    @Test
    void reusesExistingUserOnSubsequentGoogleLogin() {
        UserDocument existing = new UserDocument();
        existing.setId("user-9");
        existing.setEmail("velho@gmail.com");
        existing.setRole("OPERADOR");
        when(googleTokenVerifier.verify("id-token")).thenReturn(new GoogleUserInfo("velho@gmail.com", "Velho"));
        when(userRepository.findByEmail("velho@gmail.com")).thenReturn(Optional.of(existing));
        when(tokenService.generateToken(existing)).thenReturn("jwt2");

        LoginResponse response = service().loginWithGoogle("id-token");

        assertThat(response.token()).isEqualTo("jwt2");
        assertThat(response.role()).isEqualTo("OPERADOR");
        assertThat(response.userId()).isEqualTo("user-9");
        verify(userRepository, never()).save(any());
        verify(customerRepository, never()).save(any());
    }
}
