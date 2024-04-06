package com.serch.server.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Serch Server - Service made easy")
                        .version("1.0.0")
                        .summary("This contains the server implementations for Serch platform")
                        .description("Serch Server")
                        .termsOfService("https://legal.serchservice.com")
                        .license(new License()
                                .url("https://legal.serchservice.com")
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
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }
}