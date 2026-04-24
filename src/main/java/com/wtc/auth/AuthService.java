package com.wtc.auth;

import com.wtc.auth.dto.LoginRequest;
import com.wtc.auth.dto.LoginResponse;
import com.wtc.auth.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder; // Injetando o encriptador

    public AuthService(UserRepository repository, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest data) {
        if (repository.findByEmail(data.email()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        UserDocument newUser = new UserDocument();
        newUser.setEmail(data.email());
        // Criptografa a senha antes de salvar!
        newUser.setPassword(passwordEncoder.encode(data.password()));
        newUser.setRole(data.role());

        repository.save(newUser);
    }

    public LoginResponse login(LoginRequest data) {
        var user = repository.findByEmail(data.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Compara a senha digitada com a senha criptografada do banco
        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = tokenService.generateToken(user);
        return new LoginResponse(token, user.getRole());
    }
}