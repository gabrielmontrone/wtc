package com.wtc.message;

import com.wtc.message.dto.MessageResponse;
import com.wtc.message.dto.SendMessageRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {


    private final SendMessageService sendMessageService;

    private final ListMessageService listMessageService;

    public MessageController(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
        this.listMessageService = new ListMessageService();
    }

    @PostMapping
    public ResponseEntity<MessageResponse> send(@Valid @RequestBody SendMessageRequest request) {
        MessageResponse response = sendMessageService.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // --- MTD Historico de conversas ---
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageResponse>> getHistory(@PathVariable String conversationId) {
        var history = listMessageService.execute(conversationId);
        return ResponseEntity.ok(history);
    }
}