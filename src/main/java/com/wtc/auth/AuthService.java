package com.wtc.auth;

import com.wtc.auth.dto.LoginRequest;
import com.wtc.auth.dto.LoginResponse;
import com.wtc.auth.dto.RegisterRequest;
import com.wtc.audit.AuditService;
import com.wtc.customer.CustomerDocument; // Certifique-se que o import está correto
import com.wtc.customer.CustomerRepository; // Certifique-se que o import está correto
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository repository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final CustomerRepository customerRepository; // Injetando o repositório de clientes

    public AuthService(UserRepository repository,
                       TokenService tokenService,
                       PasswordEncoder passwordEncoder,
                       AuditService auditService,
                       CustomerRepository customerRepository) {
        this.repository = repository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
        this.customerRepository = customerRepository;
    }

    public void register(RegisterRequest data) {
        if (repository.findByEmail(data.email()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        // 1. Criar e salvar o usuário (Acesso ao Sistema)
        UserDocument newUser = new UserDocument();
        newUser.setEmail(data.email());
        newUser.setPassword(passwordEncoder.encode(data.password()));
        newUser.setRole(data.role().toUpperCase()); // Garante que salve como CLIENTE ou OPERADOR

        repository.save(newUser);

        // 2. SE FOR CLIENTE, cria automaticamente o perfil na base de empresas (Customers)
        if ("CLIENTE".equalsIgnoreCase(data.role())) {
            CustomerDocument customer = new CustomerDocument();
            // Vinculamos o ID do Customer ao ID do User para facilitar o Chat
            customer.setId(newUser.getId());
            customer.setName("Empresa: " + data.email().split("@")[0]);
            customer.setDocument("CNPJ Pendente");
            customer.setAtivo(true);
            customer.setVip(false);
            customer.setFidelidade(false);
            customer.setCreatedAt(LocalDateTime.now());

            customerRepository.save(customer);
            auditService.log("CREATE_CUSTOMER", data.email(), "Perfil comercial criado automaticamente para o novo usuário.");
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

        // RETORNANDO O ID DO USUÁRIO (Vital para o Android)
        return new LoginResponse(token, user.getRole(), user.getId());
    }
}