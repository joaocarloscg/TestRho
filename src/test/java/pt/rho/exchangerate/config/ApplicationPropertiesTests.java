package pt.rho.exchangerate.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplicationPropertiesTests {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Test
    void shouldBindApplicationProperties() {
        assertThat(applicationProperties).isNotNull();
        assertThat(applicationProperties.getBaseUrl()).isNotBlank();
        assertThat(applicationProperties.getTimeoutSeconds()).isGreaterThan(0);
    }
}