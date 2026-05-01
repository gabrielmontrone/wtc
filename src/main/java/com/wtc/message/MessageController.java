package com.wtc.message;

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
@Tag(name = "Messages", description = "Message dispatch and history")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {


    private final SendMessageService sendMessageService;

    private final ListMessageService listMessageService;

    public MessageController(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
        this.listMessageService = new ListMessageService();
    }

    @PostMapping
    @Operation(summary = "Send message", description = "Dispatches a message to customers, a segment, or a group.")
    public ResponseEntity<MessageResponse> send(@Valid @RequestBody SendMessageRequest request) {
        MessageResponse response = sendMessageService.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // --- MTD Historico de conversas ---
    @GetMapping("/conversation/{conversationId}")
    @Operation(summary = "Get conversation message history", description = "Returns the messages for a conversation.")
    public ResponseEntity<List<MessageResponse>> getHistory(@Parameter(description = "Conversation ID") @PathVariable String conversationId) {
        var history = listMessageService.execute(conversationId);
        return ResponseEntity.ok(history);
    }
}
