package com.wtc.notification;

import com.wtc.auth.UserRepository;
import com.wtc.auth.UserDocument;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notificações", description = "Gerenciamento de tokens push (FCM)")
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final UserRepository userRepository;

    public NotificationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register-token")
    public ResponseEntity<String> registerToken(@RequestBody String token) {
        // Pega o usuário logado
        UserDocument currentUser = (UserDocument) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        currentUser.setFcmToken(token);
        userRepository.save(currentUser);

        return ResponseEntity.ok("Token registrado com sucesso!");
    }
}