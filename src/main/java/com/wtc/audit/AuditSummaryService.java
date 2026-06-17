package com.wtc.audit;

import com.wtc.audit.dto.AuditSummaryResponse;
import com.wtc.audit.dto.AuditSummaryResponse.LabelCount;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aggregates audit events into the dashboard summary. The window is optional: when {@code from}
 * and/or {@code to} are given, only events in that range are considered; otherwise the whole
 * collection is used.
 */
@Service
public class AuditSummaryService {

    private static final String ACTION_SUSPICIOUS = "SUSPICIOUS_MESSAGE";
    private static final String ACTION_LOGIN_FAILED = "LOGIN_FAILED";
    private static final String ACTION_SEND_MESSAGE = "SEND_MESSAGE";
    private static final int TOP_USERS_LIMIT = 5;

    private final AuditLogRepository repository;

    public AuditSummaryService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public AuditSummaryResponse summarize(String from, String to) {
        List<AuditLogDocument> events = fetch(from, to);

        long total = events.size();
        long suspicious = countAction(events, ACTION_SUSPICIOUS);
        long failedLogins = countAction(events, ACTION_LOGIN_FAILED);
        long sent = countAction(events, ACTION_SEND_MESSAGE);
        int rate = sent > 0 ? (int) (suspicious * 100 / sent) : 0;

        return new AuditSummaryResponse(
                total,
                suspicious,
                failedLogins,
                sent,
                rate,
                countsByAction(events),
                riskDistribution(events),
                topUsers(events));
    }

    private List<AuditLogDocument> fetch(String from, String to) {
        boolean hasFrom = from != null && !from.isBlank();
        boolean hasTo = to != null && !to.isBlank();
        if (hasFrom && hasTo) {
            return repository.findByTimestampBetween(from, to);
        }
        if (hasFrom) {
            return repository.findByTimestampGreaterThanEqual(from);
        }
        if (hasTo) {
            return repository.findByTimestampLessThanEqual(to);
        }
        return repository.findAll();
    }

    private long countAction(List<AuditLogDocument> events, String action) {
        return events.stream().filter(e -> action.equals(e.getAction())).count();
    }

    private List<LabelCount> countsByAction(List<AuditLogDocument> events) {
        return toSortedLabelCounts(
                events.stream()
                        .filter(e -> e.getAction() != null)
                        .collect(Collectors.groupingBy(AuditLogDocument::getAction, Collectors.counting())));
    }

    private List<LabelCount> riskDistribution(List<AuditLogDocument> events) {
        return toSortedLabelCounts(
                events.stream()
                        .filter(e -> ACTION_SUSPICIOUS.equals(e.getAction()))
                        .collect(Collectors.groupingBy(
                                e -> e.getRiskLevel() != null ? e.getRiskLevel() : "N/D",
                                Collectors.counting())));
    }

    private List<LabelCount> topUsers(List<AuditLogDocument> events) {
        Map<String, Long> byUser =
                events.stream()
                        .map(AuditLogDocument::getUserEmail)
                        .filter(email -> email != null && !email.isBlank())
                        .collect(Collectors.groupingBy(email -> email, Collectors.counting()));
        return byUser.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(TOP_USERS_LIMIT)
                .map(e -> new LabelCount(e.getKey(), e.getValue()))
                .toList();
    }

    /** Sorts a label->count map from most to least frequent and preserves that order. */
    private List<LabelCount> toSortedLabelCounts(Map<String, Long> counts) {
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new))
                .entrySet().stream()
                .map(e -> new LabelCount(e.getKey(), e.getValue()))
                .toList();
    }
}
