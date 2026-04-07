package pt.rho.exchangerate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import pt.rho.exchangerate.config.RateLimitProperties;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTests {

    @Mock
    private RateLimitProperties rateLimitProperties;

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        when(rateLimitProperties.getCapacity()).thenReturn(2L);
        when(rateLimitProperties.getRefillTokens()).thenReturn(2L);
        when(rateLimitProperties.getRefillDurationMinutes()).thenReturn(1L);
        when(rateLimitProperties.getBucketCacheMaxSize()).thenReturn(10_000L);
        when(rateLimitProperties.getBucketCacheExpireAfterAccessMinutes()).thenReturn(60L);

        rateLimitService = new RateLimitService(rateLimitProperties);
        rateLimitService.init();
    }

    @Test
    @DisplayName("Should allow request when bucket has available tokens")
    void shouldAllowRequestWhenBucketHasAvailableTokens() {
        boolean allowed = rateLimitService.tryConsume("client1");

        assertThat(allowed).isTrue();
    }

    @Test
    @DisplayName("Should block request when bucket capacity is exceeded")
    void shouldBlockRequestWhenBucketCapacityIsExceeded() {
        boolean firstRequest = rateLimitService.tryConsume("client1");
        boolean secondRequest = rateLimitService.tryConsume("client1");
        boolean thirdRequest = rateLimitService.tryConsume("client1");

        assertThat(firstRequest).isTrue();
        assertThat(secondRequest).isTrue();
        assertThat(thirdRequest).isFalse();
    }

    @Test
    @DisplayName("Should track rate limits independently for different client keys")
    void shouldTrackRateLimitsIndependentlyForDifferentClientKeys() {
        boolean firstClientFirstRequest = rateLimitService.tryConsume("client1");
        boolean firstClientSecondRequest = rateLimitService.tryConsume("client1");
        boolean firstClientThirdRequest = rateLimitService.tryConsume("client1");

        boolean secondClientFirstRequest = rateLimitService.tryConsume("client2");
        boolean secondClientSecondRequest = rateLimitService.tryConsume("client2");

        assertThat(firstClientFirstRequest).isTrue();
        assertThat(firstClientSecondRequest).isTrue();
        assertThat(firstClientThirdRequest).isFalse();

        assertThat(secondClientFirstRequest).isTrue();
        assertThat(secondClientSecondRequest).isTrue();
    }
}