package com.wtc.compliance;

/**
 * A single sensitive-data / safety finding in a message.
 *
 * @param type        machine code (CPF, CNPJ, CARD, SUSPICIOUS_LINK)
 * @param description human-readable explanation, used in the audit trail
 */
public record RiskFlag(String type, String description) {

    public static final String CPF = "CPF";
    public static final String CNPJ = "CNPJ";
    public static final String CARD = "CARD";
    public static final String SUSPICIOUS_LINK = "SUSPICIOUS_LINK";
}
