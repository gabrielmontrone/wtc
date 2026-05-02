package com.wtc.attachment;

import com.wtc.attachment.dto.UploadRequest;
import com.wtc.attachment.dto.UploadResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload-request")
    public ResponseEntity<UploadResponse> createUploadRequest(@RequestBody @Valid UploadRequest request) {
        UploadResponse response = attachmentService.prepareUpload(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{attachmentId}/confirm")
    public ResponseEntity<Void> confirmUpload(
            @PathVariable String attachmentId,
            @RequestParam String messageId) {
        attachmentService.confirmUpload(attachmentId, messageId);
        return ResponseEntity.ok().build();
    }

}