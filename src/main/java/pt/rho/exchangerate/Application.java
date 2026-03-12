package pt.rho.exchangerate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import pt.rho.exchangerate.config.ApplicationProperties;
import pt.rho.exchangerate.config.RateLimitProperties;

@SpringBootApplication
@EnableConfigurationProperties({ ApplicationProperties.class, RateLimitProperties.class })
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}