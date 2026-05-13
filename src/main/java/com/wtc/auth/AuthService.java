package com.wtc.auth;

import com.wtc.auth.dto.LoginRequest;
import com.wtc.auth.dto.LoginResponse;
import com.wtc.auth.dto.RegisterRequest;
import com.wtc.audit.AuditService; // Importante: adicione o import
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService; // 1. Injetando o serviço de auditoria

    public AuthService(UserRepository repository, TokenService tokenService, PasswordEncoder passwordEncoder, AuditService auditService) {
        this.repository = repository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    public void register(RegisterRequest data) {
        if (repository.findByEmail(data.email()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        UserDocument newUser = new UserDocument();
        newUser.setEmail(data.email());
        newUser.setPassword(passwordEncoder.encode(data.password()));
        newUser.setRole(data.role());

        repository.save(newUser);

        // 2. Registrando auditoria de novo cadastro
        auditService.log("CREATE_USER", data.email(), "Novo usuário registrado com role: " + data.role());
    }

    public LoginResponse login(LoginRequest data) {
        var user = repository.findByEmail(data.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            // Auditoria de tentativa de senha errada (opcional, mas muito bom para segurança)
            auditService.log("LOGIN_FAILED", data.email(), "Tentativa de login com senha inválida");
            throw new RuntimeException("Senha inválida");
        }

        String token = tokenService.generateToken(user);

        // 3. Registrando auditoria de login com sucesso
        auditService.log("LOGIN_SUCCESS", user.getEmail(), "Usuário autenticado com sucesso via JWT");

        return new LoginResponse(token, user.getRole());
    }
}