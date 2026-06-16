package com.wtc.compliance;

/**
 * Severity of a message flagged by the DLP / Trust &amp; Safety scan. Ordered from
 * lowest to highest so the analyzer can take the maximum across multiple findings.
 */
public enum RiskLevel {
    NONE,
    LOW,
    MEDIUM,
    HIGH;

    /** A message is treated as suspicious (audited + warned) at MEDIUM or above. */
    public boolean isSuspicious() {
        return this.ordinal() >= MEDIUM.ordinal();
    }

    public RiskLevel max(RiskLevel other) {
        return this.ordinal() >= other.ordinal() ? this : other;
    }
}
