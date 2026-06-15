package com.wtc.auth;

import com.wtc.auth.dto.LoginRequest;
import com.wtc.auth.dto.LoginResponse;
import com.wtc.auth.dto.RegisterRequest;
import com.wtc.audit.AuditService;
import com.wtc.customer.CustomerDocument;
import com.wtc.customer.CustomerRepository;
import com.wtc.conversation.ConversationDocument; // Importe sua classe de conversa
import com.wtc.conversation.ConversationRepository; // Importe seu repositório de conversa
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository repository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final CustomerRepository customerRepository;
    private final ConversationRepository conversationRepository; // 1. Adicionado repositório

    public AuthService(UserRepository repository,
                       TokenService tokenService,
                       PasswordEncoder passwordEncoder,
                       AuditService auditService,
                       CustomerRepository customerRepository,
                       ConversationRepository conversationRepository) { // 2. Injetado no construtor
        this.repository = repository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
        this.customerRepository = customerRepository;
        this.conversationRepository = conversationRepository;
    }

    public void register(RegisterRequest data) {
        if (repository.findByEmail(data.email()).isPresent()) {
            throw new EmailAlreadyExistsException("E-mail já cadastrado");
        }

        // 1. Criar e salvar o usuário (Acesso ao Sistema)
        UserDocument newUser = new UserDocument();
        newUser.setEmail(data.email());
        newUser.setPassword(passwordEncoder.encode(data.password()));
        newUser.setRole(data.role().toUpperCase());

        repository.save(newUser);

        // 2. SE FOR CLIENTE, cria automaticamente o perfil comercial e a conversa inicial
        if ("CLIENTE".equalsIgnoreCase(data.role())) {

            // Criar Perfil de Cliente
            CustomerDocument customer = new CustomerDocument();
            customer.setId(newUser.getId()); // ID igual ao do Usuário
            customer.setName("Empresa: " + data.email().split("@")[0]);
            customer.setDocument("CNPJ Pendente");
            customer.setAtivo(true);
            customer.setVip(false);
            customer.setFidelidade(false);
            customer.setCreatedAt(LocalDateTime.now());
            customerRepository.save(customer);

            // 3. CRIAÇÃO DA CONVERSA (ConversationDocument)
            // Isso garante que o GET /messages/conversation/{id} do Android funcione
            ConversationDocument conversation = new ConversationDocument();
            conversation.setId(newUser.getId()); // ID da conversa igual ao ID do Cliente
            conversation.setCustomerId(newUser.getId());
            conversation.setStatus("OPEN");
            conversation.setUpdatedAt(Instant.now());
            conversationRepository.save(conversation);

            auditService.log("CREATE_CUSTOMER", data.email(), "Perfil e Conversa criados automaticamente.");
        }

        auditService.log("CREATE_USER", data.email(), "Novo usuário registrado com role: " + data.role());
    }

    public LoginResponse login(LoginRequest data) {
        var user = repository.findByEmail(data.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            auditService.log("LOGIN_FAILED", data.email(), "Tentativa de login com senha inválida");
            throw new RuntimeException("Senha inválida");
        }

        String token = tokenService.generateToken(user);
        auditService.log("LOGIN_SUCCESS", user.getEmail(), "Usuário autenticado com sucesso via JWT");

        // Retorna o ID real do MongoDB
        return new LoginResponse(token, user.getRole(), user.getId());
    }
}