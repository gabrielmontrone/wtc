package com.wtc.auth;

import com.wtc.auth.dto.LoginRequest;
import com.wtc.auth.dto.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;
    private final TokenService tokenService;

    public AuthService(UserRepository repository, TokenService tokenService) {
        this.repository = repository;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest data) {
        // Busca o usuário pelo e-mail
        var user = repository.findByEmail(data.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verificação de senha (IMPORTANTE: Na vida real usamos BCrypt,
        // mas para o seu teste inicial vamos comparar o texto puro)
        if (!data.password().equals(user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        // Gera o Token
        String token = tokenService.generateToken(user);

        return new LoginResponse(token, user.getRole());
    }
}