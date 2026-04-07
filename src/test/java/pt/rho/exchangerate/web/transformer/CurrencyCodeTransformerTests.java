package pt.rho.exchangerate.web.transformer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyCodeTransformerTests {

    private final CurrencyCodeTransformer transformer = new CurrencyCodeTransformer();

    @Test
    void shouldTrimAndUppercaseCurrency() {
        assertThat(transformer.convert(" usd ")).isEqualTo("USD");
    }

    @Test
    void shouldHandleLowercaseCurrency() {
        assertThat(transformer.convert("eur")).isEqualTo("EUR");
    }

    @Test
    void shouldHandleAlreadyUppercaseCurrency() {
        assertThat(transformer.convert("GBP")).isEqualTo("GBP");
    }

    @Test
    void shouldReturnNullWhenSourceIsNull() {
        assertThat(transformer.convert(null)).isNull();
    }
}