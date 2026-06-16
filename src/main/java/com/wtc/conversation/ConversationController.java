package com.wtc.conversation;

import com.wtc.conversation.dto.ConversationResponse;
import com.wtc.conversation.dto.CreateConversationRequest;
import com.wtc.message.ChatMessageService;
import com.wtc.message.dto.ChatMessageRequest;
import com.wtc.message.dto.MessageResponse;
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
@RequestMapping("/api/v1/conversations")
@Tag(name = "Conversas", description = "Conversas com clientes")
@SecurityRequirement(name = "bearerAuth")
public class ConversationController {

    private final ListConversationService listConversationService;
    private final ChatMessageService chatMessageService;
    private final CreateConversationService createConversationService;

    // Construtor atualizado injetando os serviços necessários
    public ConversationController(ListConversationService listConversationService,
                                  ChatMessageService chatMessageService,
                                  CreateConversationService createConversationService) {
        this.listConversationService = listConversationService;
        this.chatMessageService = chatMessageService;
        this.createConversationService = createConversationService;
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Listar conversas do cliente", description = "Retorna todas as conversas de um cliente.")
    public ResponseEntity<List<ConversationResponse>> getList(@Parameter(description = "ID do cliente") @PathVariable String customerId) {
        List<ConversationResponse> conversations = listConversationService.execute(customerId);
        return ResponseEntity.ok(conversations);
    }

    @PostMapping
    @Operation(summary = "Iniciar conversa", description = "Cria uma nova conversa ABERTA para um cliente existente.")
    public ResponseEntity<ConversationResponse> create(@RequestBody @Valid CreateConversationRequest request) {
        ConversationResponse created = createConversationService.execute(request.customerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{conversationId}/messages")
    @Operation(summary = "Enviar mensagem/resposta", description = "Envia uma nova mensagem em uma conversa existente (chat bidirecional).")
    public ResponseEntity<MessageResponse> sendReply(
            @Parameter(description = "ID da conversa") @PathVariable String conversationId,
            @RequestBody @Valid ChatMessageRequest request) {
        return ResponseEntity.ok(chatMessageService.sendReply(conversationId, request));
    }
}