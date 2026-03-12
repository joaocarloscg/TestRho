package pt.rho.exchangerate.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

	@Bean
	public RestClient ratesProviderRestClient(ApplicationProperties applicationProperties) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

		Duration timeout = Duration.ofSeconds(applicationProperties.getTimeoutSeconds());
		requestFactory.setConnectTimeout(timeout);
		requestFactory.setReadTimeout(timeout);

		return RestClient
				.builder()
				.baseUrl(applicationProperties.getBaseUrl())
				.requestFactory(requestFactory)
				.build();
	}
}