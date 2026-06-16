package com.wtc.auth;

import com.wtc.auth.dto.LoginRequest;
import com.wtc.auth.dto.LoginResponse;
import com.wtc.auth.dto.RegisterRequest;
import com.wtc.auth.dto.RegisterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints para login e registro de novos usuários")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar Login", description = "Valida as credenciais e devolve um Token JWT válido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos")
    })
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest data) {
        var response = authService.login(data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Criar nova conta", description = "Cadastra um CLIENTE ou OPERADOR com senha criptografada.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest data) {
        this.authService.register(data);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RegisterResponse("Usuário cadastrado com sucesso!", data.role().toUpperCase()));
    }
}