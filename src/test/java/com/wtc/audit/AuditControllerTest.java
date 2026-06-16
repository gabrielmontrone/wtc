package com.wtc.audit;

import com.wtc.audit.dto.AuditResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

    @Mock
    private AuditLogRepository repository;

    @InjectMocks
    private AuditController controller;

    @Test
    void mapsRecentAuditLogsToResponses() {
        AuditLogDocument doc = new AuditLogDocument();
        doc.setId("a1");
        doc.setAction("SUSPICIOUS_MESSAGE");
        doc.setUserEmail("ana@wtc.com");
        doc.setDetails("Conversa 9 — risco HIGH [cartão]");
        doc.setTimestamp("2026-06-16T12:00:00Z");
        when(repository.findTop100ByOrderByTimestampDesc()).thenReturn(List.of(doc));

        ResponseEntity<List<AuditResponse>> response = controller.list();

        assertThat(response.getBody()).hasSize(1);
        AuditResponse first = response.getBody().get(0);
        assertThat(first.action()).isEqualTo("SUSPICIOUS_MESSAGE");
        assertThat(first.userEmail()).isEqualTo("ana@wtc.com");
        assertThat(first.timestamp()).isEqualTo("2026-06-16T12:00:00Z");
    }

    @Test
    void returnsEmptyListWhenNoLogs() {
        when(repository.findTop100ByOrderByTimestampDesc()).thenReturn(List.of());

        assertThat(controller.list().getBody()).isEmpty();
    }
}
