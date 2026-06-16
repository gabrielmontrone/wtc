package com.wtc.compliance;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DLP / Trust &amp; Safety engine: scans free-text message content for sensitive data
 * (CPF, CNPJ, card numbers) and risky links, returning a {@link RiskAnalysis}.
 *
 * <p>Pure and deterministic (no I/O) so it is fully unit-testable. The bounded
 * lookarounds keep a CPF inside a longer card number from being double-counted.
 */
@Component
public class RiskAnalyzer {

    // CPF: 000.000.000-00 (separators optional), not part of a longer digit run.
    private static final Pattern CPF =
            Pattern.compile("(?<!\\d)\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}(?!\\d)");

    // CNPJ: 00.000.000/0000-00 (separators optional).
    private static final Pattern CNPJ =
            Pattern.compile("(?<!\\d)\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}(?!\\d)");

    // Candidate card: 13–19 digits with optional single space/dash separators.
    private static final Pattern CARD_CANDIDATE =
            Pattern.compile("(?<!\\d)\\d(?:[ -]?\\d){12,18}(?!\\d)");

    // URLs (http/https or bare www.).
    private static final Pattern URL =
            Pattern.compile("(?i)\\b(?:https?://|www\\.)\\S+");

    private static final Set<String> URL_SHORTENERS = Set.of(
            "bit.ly", "tinyurl.com", "t.co", "goo.gl", "ow.ly", "is.gd", "buff.ly", "cutt.ly", "rebrand.ly");

    private static final Pattern RAW_IP_URL =
            Pattern.compile("(?i)https?://\\d{1,3}(?:\\.\\d{1,3}){3}");

    public RiskAnalysis analyze(String content) {
        if (content == null || content.isBlank()) {
            return RiskAnalysis.clean();
        }

        List<RiskFlag> flags = new ArrayList<>();
        RiskLevel level = RiskLevel.NONE;

        if (containsValidCard(content)) {
            flags.add(new RiskFlag(RiskFlag.CARD, "Possível número de cartão de crédito"));
            level = level.max(RiskLevel.HIGH);
        }
        if (CPF.matcher(content).find()) {
            flags.add(new RiskFlag(RiskFlag.CPF, "Possível CPF"));
            level = level.max(RiskLevel.MEDIUM);
        }
        if (CNPJ.matcher(content).find()) {
            flags.add(new RiskFlag(RiskFlag.CNPJ, "Possível CNPJ"));
            level = level.max(RiskLevel.MEDIUM);
        }
        if (containsSuspiciousLink(content)) {
            flags.add(new RiskFlag(RiskFlag.SUSPICIOUS_LINK, "Link potencialmente suspeito"));
            level = level.max(RiskLevel.MEDIUM);
        }

        return new RiskAnalysis(level, List.copyOf(flags));
    }

    private boolean containsValidCard(String content) {
        Matcher m = CARD_CANDIDATE.matcher(content);
        while (m.find()) {
            String digits = m.group().replaceAll("[ -]", "");
            if (digits.length() >= 13 && digits.length() <= 19 && isLuhnValid(digits)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLuhnValid(String digits) {
        int sum = 0;
        boolean doubleDigit = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int d = digits.charAt(i) - '0';
            if (doubleDigit) {
                d *= 2;
                if (d > 9) {
                    d -= 9;
                }
            }
            sum += d;
            doubleDigit = !doubleDigit;
        }
        return sum % 10 == 0;
    }

    private boolean containsSuspiciousLink(String content) {
        if (RAW_IP_URL.matcher(content).find()) {
            return true;
        }
        Matcher m = URL.matcher(content);
        while (m.find()) {
            String url = m.group();
            String host = extractHost(url);
            // Non-HTTPS links or known URL shorteners are treated as suspicious.
            if (url.regionMatches(true, 0, "http://", 0, "http://".length())) {
                return true;
            }
            if (URL_SHORTENERS.contains(host)) {
                return true;
            }
        }
        return false;
    }

    private String extractHost(String url) {
        String host = url.replaceFirst("(?i)^https?://", "").replaceFirst("(?i)^www\\.", "");
        int slash = host.indexOf('/');
        if (slash >= 0) {
            host = host.substring(0, slash);
        }
        int colon = host.indexOf(':');
        if (colon >= 0) {
            host = host.substring(0, colon);
        }
        return host.toLowerCase();
    }
}
