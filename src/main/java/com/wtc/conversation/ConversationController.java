package com.wtc.conversation;

import com.wtc.conversation.dto.ConversationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    private final ListConversationService listConversationService;

    public ConversationController(ListConversationService listConversationService) {
        this.listConversationService = listConversationService;
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ConversationResponse>> getList(@PathVariable String customerId) {
        List<ConversationResponse> conversations = listConversationService.execute(customerId);
        return ResponseEntity.ok(conversations);
    }
}