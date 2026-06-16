package com.wtc.compliance;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RiskAnalyzerTest {

    private final RiskAnalyzer analyzer = new RiskAnalyzer();

    @Test
    void cleanMessageHasNoRisk() {
        RiskAnalysis result = analyzer.analyze("Olá, podemos agendar uma reunião amanhã às 10h?");

        assertThat(result.level()).isEqualTo(RiskLevel.NONE);
        assertThat(result.flags()).isEmpty();
        assertThat(result.isSuspicious()).isFalse();
    }

    @Test
    void blankOrNullIsClean() {
        assertThat(analyzer.analyze(null).level()).isEqualTo(RiskLevel.NONE);
        assertThat(analyzer.analyze("   ").level()).isEqualTo(RiskLevel.NONE);
    }

    @Test
    void detectsFormattedCpfAsMedium() {
        RiskAnalysis result = analyzer.analyze("Meu CPF é 529.982.247-25 para cadastro");

        assertThat(result.level()).isEqualTo(RiskLevel.MEDIUM);
        assertThat(result.flagTypes()).contains(RiskFlag.CPF);
    }

    @Test
    void detectsUnformattedCpf() {
        assertThat(analyzer.analyze("segue 52998224725 ok").flagTypes()).contains(RiskFlag.CPF);
    }

    @Test
    void detectsCnpj() {
        RiskAnalysis result = analyzer.analyze("CNPJ 11.222.333/0001-81 da empresa");

        assertThat(result.level()).isEqualTo(RiskLevel.MEDIUM);
        assertThat(result.flagTypes()).contains(RiskFlag.CNPJ);
    }

    @Test
    void detectsValidCardAsHigh() {
        RiskAnalysis result = analyzer.analyze("cartão 4111 1111 1111 1111 validade 12/29");

        assertThat(result.level()).isEqualTo(RiskLevel.HIGH);
        assertThat(result.flagTypes()).contains(RiskFlag.CARD);
    }

    @Test
    void ignoresDigitSequenceThatFailsLuhn() {
        RiskAnalysis result = analyzer.analyze("pedido número 1234 5678 9012 3456 confirmado");

        assertThat(result.flagTypes()).doesNotContain(RiskFlag.CARD);
        assertThat(result.level()).isEqualTo(RiskLevel.NONE);
    }

    @Test
    void flagsHttpLinkAsSuspicious() {
        RiskAnalysis result = analyzer.analyze("acesse http://promo.example.com agora");

        assertThat(result.flagTypes()).contains(RiskFlag.SUSPICIOUS_LINK);
        assertThat(result.isSuspicious()).isTrue();
    }

    @Test
    void flagsUrlShortener() {
        assertThat(analyzer.analyze("clique em https://bit.ly/abc123").flagTypes())
                .contains(RiskFlag.SUSPICIOUS_LINK);
    }

    @Test
    void allowsPlainHttpsLink() {
        RiskAnalysis result = analyzer.analyze("nosso site é https://www.wtc.com.br/contato");

        assertThat(result.flagTypes()).doesNotContain(RiskFlag.SUSPICIOUS_LINK);
        assertThat(result.level()).isEqualTo(RiskLevel.NONE);
    }

    @Test
    void takesMaxLevelAcrossMultipleFindings() {
        RiskAnalysis result = analyzer.analyze("CPF 529.982.247-25 e cartão 4111111111111111");

        assertThat(result.level()).isEqualTo(RiskLevel.HIGH);
        assertThat(result.flagTypes()).contains(RiskFlag.CPF, RiskFlag.CARD);
    }
}
