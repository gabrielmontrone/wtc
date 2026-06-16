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
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository repository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final CustomerRepository customerRepository;
    private final ConversationRepository conversationRepository; // 1. Adicionado repositório
    private final GoogleTokenVerifier googleTokenVerifier;

    public AuthService(UserRepository repository,
                       TokenService tokenService,
                       PasswordEncoder passwordEncoder,
                       AuditService auditService,
                       CustomerRepository customerRepository,
                       ConversationRepository conversationRepository, // 2. Injetado no construtor
                       GoogleTokenVerifier googleTokenVerifier) {
        this.repository = repository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
        this.customerRepository = customerRepository;
        this.conversationRepository = conversationRepository;
        this.googleTokenVerifier = googleTokenVerifier;
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
            bootstrapClienteProfile(newUser);
        }

        auditService.log("CREATE_USER", data.email(), "Novo usuário registrado com role: " + data.role());
    }

    /**
     * Cria o perfil comercial (CustomerDocument) e a conversa inicial de um CLIENTE,
     * garantindo que o GET /messages/conversation/{id} do Android funcione.
     */
    private void bootstrapClienteProfile(UserDocument user) {
        CustomerDocument customer = new CustomerDocument();
        customer.setId(user.getId()); // ID igual ao do Usuário
        customer.setName("Empresa: " + user.getEmail().split("@")[0]);
        customer.setDocument("CNPJ Pendente");
        customer.setAtivo(true);
        customer.setVip(false);
        customer.setFidelidade(false);
        customer.setCreatedAt(LocalDateTime.now());
        customerRepository.save(customer);

        ConversationDocument conversation = new ConversationDocument();
        conversation.setId(user.getId()); // ID da conversa igual ao ID do Cliente
        conversation.setCustomerId(user.getId());
        conversation.setStatus("OPEN");
        conversation.setUpdatedAt(Instant.now());
        conversationRepository.save(conversation);

        auditService.log("CREATE_CUSTOMER", user.getEmail(), "Perfil e Conversa criados automaticamente.");
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

    /**
     * Autentica via Google Sign-In: valida o ID token, encontra o usuário pelo e-mail
     * ou cria um novo CLIENTE na primeira vez, e devolve um JWT da aplicação.
     */
    public LoginResponse loginWithGoogle(String idToken) {
        GoogleUserInfo googleUser = googleTokenVerifier.verify(idToken);

        UserDocument user = repository.findByEmail(googleUser.email())
                .orElseGet(() -> createGoogleUser(googleUser.email()));

        String token = tokenService.generateToken(user);
        auditService.log("LOGIN_SUCCESS", user.getEmail(), "Usuário autenticado via Google Sign-In");

        return new LoginResponse(token, user.getRole(), user.getId());
    }

    private UserDocument createGoogleUser(String email) {
        UserDocument newUser = new UserDocument();
        newUser.setEmail(email);
        // Conta sem senha local: gravamos um hash aleatório para inviabilizar login por senha.
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        newUser.setRole("CLIENTE");
        repository.save(newUser);

        bootstrapClienteProfile(newUser);
        auditService.log("CREATE_USER", email, "Novo usuário criado via Google Sign-In (CLIENTE)");
        return newUser;
    }
}