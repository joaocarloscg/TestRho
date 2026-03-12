package pt.rho.exchangerate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI applicationOpenApi(AuthProperties authProperties) {
        OpenAPI openAPI = new OpenAPI()
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

        if (authProperties.isEnabled()) {
            openAPI
                    .components(new Components()
                            .addSecuritySchemes("apiKeyAuth", new SecurityScheme()
                                    .type(SecurityScheme.Type.APIKEY)
                                    .in(SecurityScheme.In.HEADER)
                                    .name(authProperties.getHeaderName())))
                    .addSecurityItem(new SecurityRequirement().addList("apiKeyAuth"));
        }

        return openAPI;
    }
}