package com.wtc.compliance;

import java.util.List;

/** Result of scanning one message: an overall level plus the individual findings. */
public record RiskAnalysis(RiskLevel level, List<RiskFlag> flags) {

    public static RiskAnalysis clean() {
        return new RiskAnalysis(RiskLevel.NONE, List.of());
    }

    public boolean isSuspicious() {
        return level.isSuspicious();
    }

    /** Flag type codes (e.g. ["CPF", "CARD"]) — what we persist on the message and return to the UI. */
    public List<String> flagTypes() {
        return flags.stream().map(RiskFlag::type).distinct().toList();
    }

    /** Human-readable summary for the audit trail. */
    public String summary() {
        return flags.stream().map(RiskFlag::description).distinct().toList().toString();
    }
}
