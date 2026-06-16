package com.wtc.attachment;

import com.wtc.attachment.dto.UploadRequest;
import com.wtc.attachment.dto.UploadResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository repository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private AttachmentService service;

    @Test
    void prepareUploadReturnsPresignedAndPublicUrlsAndPersistsMetadata() {
        UploadRequest request = new UploadRequest("foto.jpg", "image/jpeg", 1_000L);
        when(s3Service.generatePresignedUrl(anyString(), anyString())).thenReturn("https://put-url");
        when(s3Service.getPublicUrl(anyString())).thenReturn("https://public-url/foto.jpg");
        when(repository.save(any(AttachmentDocument.class))).thenAnswer(invocation -> {
            AttachmentDocument doc = invocation.getArgument(0);
            doc.setId("att-1");
            return doc;
        });

        UploadResponse response = service.prepareUpload(request);

        assertThat(response.attachmentId()).isEqualTo("att-1");
        assertThat(response.uploadUrl()).isEqualTo("https://put-url");
        assertThat(response.fileUrl()).isEqualTo("https://public-url/foto.jpg");

        ArgumentCaptor<AttachmentDocument> captor = ArgumentCaptor.forClass(AttachmentDocument.class);
        verify(repository).save(captor.capture());
        AttachmentDocument saved = captor.getValue();
        assertThat(saved.getFileName()).isEqualTo("foto.jpg");
        assertThat(saved.getContentType()).isEqualTo("image/jpeg");
        assertThat(saved.getUrl()).isEqualTo("https://public-url/foto.jpg");
        assertThat(saved.getStatus()).isEqualTo("PENDING");
        assertThat(saved.getS3Key()).endsWith("-foto.jpg");
    }

    @Test
    void prepareUploadRejectsFilesLargerThanTenMegabytes() {
        UploadRequest request = new UploadRequest("grande.png", "image/png", 11L * 1024 * 1024);

        assertThatThrownBy(() -> service.prepareUpload(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("10MB");

        verify(repository, never()).save(any());
    }

    @Test
    void confirmUploadMarksDocumentUploadedAndLinksMessage() {
        AttachmentDocument doc = new AttachmentDocument();
        doc.setId("att-1");
        when(repository.findById("att-1")).thenReturn(Optional.of(doc));

        service.confirmUpload("att-1", "msg-9");

        assertThat(doc.getStatus()).isEqualTo("UPLOADED");
        assertThat(doc.getMessageId()).isEqualTo("msg-9");
        verify(repository).save(doc);
    }
}
