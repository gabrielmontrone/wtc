package com.wtc.message;

import com.wtc.auth.AccessControlService;
import com.wtc.message.dto.MessageResponse;
import com.wtc.message.dto.SendMessageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@Tag(name = "Mensagens", description = "Disparo e histórico de mensagens")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {


    private final SendMessageService sendMessageService;

    private final ListMessageService listMessageService;

    private final AccessControlService accessControl;

    public MessageController(SendMessageService sendMessageService, ListMessageService listMessageService,
                             AccessControlService accessControl) {
        this.sendMessageService = sendMessageService;
        this.listMessageService = listMessageService;
        this.accessControl = accessControl;
    }

    @PostMapping
    @Operation(summary = "Enviar mensagem", description = "Dispara uma mensagem para clientes, um segmento ou um grupo.")
    public ResponseEntity<MessageResponse> send(@Valid @RequestBody SendMessageRequest request) {
        MessageResponse response = sendMessageService.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // --- MTD Historico de conversas ---
    @GetMapping("/conversation/{conversationId}")
    @Operation(summary = "Obter histórico de mensagens da conversa", description = "Retorna as mensagens de uma conversa.")
    public ResponseEntity<List<MessageResponse>> getHistory(@Parameter(description = "ID da conversa") @PathVariable String conversationId) {
        accessControl.checkConversationAccess(conversationId);
        var history = listMessageService.execute(conversationId);
        return ResponseEntity.ok(history);
    }
}
