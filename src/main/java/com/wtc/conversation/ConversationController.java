package com.wtc.conversation;

import com.wtc.conversation.dto.ConversationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
@Tag(name = "Conversations", description = "Customer conversations")
@SecurityRequirement(name = "bearerAuth")
public class ConversationController {

    private final ListConversationService listConversationService;

    public ConversationController(ListConversationService listConversationService) {
        this.listConversationService = listConversationService;
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "List customer conversations", description = "Returns all conversations for a customer.")
    public ResponseEntity<List<ConversationResponse>> getList(@Parameter(description = "Customer ID") @PathVariable String customerId) {
        List<ConversationResponse> conversations = listConversationService.execute(customerId);
        return ResponseEntity.ok(conversations);
    }
}
