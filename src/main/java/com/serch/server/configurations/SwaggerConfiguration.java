package com.serch.server.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * The SwaggerConfiguration class configures Swagger documentation settings for the application.
 * It is responsible for defining the OpenAPI specification, including information about the API,
 * security schemes, and contact details.
 *
 * @see org.springframework.context.annotation.Configuration
 */
@Configuration
public class SwaggerConfiguration {
    @Value("${application.base-url}")
    private String SERCH_BASE_URL;

    /**
     * Configures custom OpenAPI specification for the application.
     *
     * @return An instance of OpenAPI containing custom API documentation settings.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Serch Server - Service made easy")
                        .version("1.0.0")
                        .summary("This contains the server implementations for Serch platform")
                        .description("Serch Server")
                        .termsOfService("https://www.serchservice.com/hub/legal")
                        .license(new License()
                                .url("https://www.serchservice.com/hub/legal")
                                .name("Serch Server License")
                                .identifier("server")
                        )
                        .contact(new Contact()
                                .name("Evaristus Adimonyemma")
                                .url("https://www.linkedin.com/in/iamevaristus")
                                .email("evaristusadimonyemma@gmail.com")
                        )
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "Bearer Authentication",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("Bearer")
                                                .bearerFormat("JWT")
                                )
                )
                .servers(List.of(
                        new Server()
                                .description("Development Server")
                                .url(String.format("%s/dev", SERCH_BASE_URL)),
                        new Server()
                                .description("Production Server")
                                .url(SERCH_BASE_URL)
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }
}