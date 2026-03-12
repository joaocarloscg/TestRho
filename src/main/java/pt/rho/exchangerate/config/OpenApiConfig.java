package pt.rho.exchangerate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI applicationOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Exchange Rate API")
						.description("API for retrieving exchange rates and performing currency conversions")
						.version("v1")
						.contact(new Contact()
								.name("João Gonçalves")
								.email("joaocarloscg@gmail.com"))
						.license(new License()
								.name("For technical assessment use")))
				.externalDocs(new ExternalDocumentation()
						.description("Spring Boot project documentation"));
	}
}