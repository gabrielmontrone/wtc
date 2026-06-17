package com.wtc.audit.dto;

import java.util.List;

/**
 * Aggregated view of the audit trail for the operator dashboard. Computed server-side over the
 * whole collection (or a date window), so it is accurate beyond the latest-100 list.
 */
public record AuditSummaryResponse(
        long totalEvents,
        long suspiciousCount,
        long failedLoginCount,
        long sentMessageCount,
        int suspiciousRatePercent,
        List<LabelCount> countsByAction,
        List<LabelCount> riskDistribution,
        List<LabelCount> topUsers
) {
    /** A generic "name -> count" pair reused for actions, risk levels and users. */
    public record LabelCount(String label, long count) {}
}
