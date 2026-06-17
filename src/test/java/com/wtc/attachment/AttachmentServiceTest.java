package com.wtc.attachment;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository repository;

    @InjectMocks
    private AttachmentService service;

    @Test
    void storePersistsBytesAndReturnsId() {
        when(repository.save(any(AttachmentDocument.class))).thenAnswer(invocation -> {
            AttachmentDocument doc = invocation.getArgument(0);
            doc.setId("att-1");
            return doc;
        });

        String id = service.store("foto.jpg", "image/jpeg", new byte[]{1, 2, 3});

        assertThat(id).isEqualTo("att-1");
        ArgumentCaptor<AttachmentDocument> captor = ArgumentCaptor.forClass(AttachmentDocument.class);
        verify(repository).save(captor.capture());
        AttachmentDocument saved = captor.getValue();
        assertThat(saved.getFileName()).isEqualTo("foto.jpg");
        assertThat(saved.getContentType()).isEqualTo("image/jpeg");
        assertThat(saved.getFileSize()).isEqualTo(3L);
        assertThat(saved.getContent()).hasSize(3);
    }

    @Test
    void storeRejectsEmptyFile() {
        assertThatThrownBy(() -> service.store("x.jpg", "image/jpeg", new byte[0]))
                .isInstanceOf(RuntimeException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void storeRejectsFilesLargerThanTenMegabytes() {
        byte[] big = new byte[11 * 1024 * 1024];

        assertThatThrownBy(() -> service.store("big.png", "image/png", big))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("10MB");
        verify(repository, never()).save(any());
    }

    @Test
    void getReturnsStoredAttachment() {
        AttachmentDocument doc = new AttachmentDocument();
        doc.setId("att-1");
        when(repository.findById("att-1")).thenReturn(Optional.of(doc));

        assertThat(service.get("att-1").getId()).isEqualTo("att-1");
    }
}
