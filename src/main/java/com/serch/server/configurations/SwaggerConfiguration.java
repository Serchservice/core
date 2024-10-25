package com.serch.server.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
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
    @Value("${server.servlet.context-path}")
    private String CONTEXT_PATH;

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
                        .version("1.0.8")
                        .summary("This contains the server implementations for Serch platform")
                        .description("Serch Server")
                        .termsOfService("https://www.serchservice.com/hub/legal/terms-and-conditions")
                        .license(new License()
                                .url("https://www.serchservice.com/hub/legal")
                                .name("Serch Server License")
                                .identifier("server")
                        )
                        .contact(new Contact()
                                .name("Team Serch")
                                .url("https://www.linkedin.com/company/serchservice")
                                .email("product@serchservice.com")
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
                .servers(getServers())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }

    @Data
    private static class ApiServer {
        private String name;
        private String url;
    }

    private List<Server> getServers() {
        List<ApiServer> servers = new ArrayList<>();

        ApiServer server = new ApiServer();

        server.setName("Production Server");
        server.setUrl("https://api.serchservice.com");
        servers.add(server);

        server.setName("Sandbox Server");
        server.setUrl("https://sandbox.serchservice.com");
        servers.add(server);

//        server.setName("Local Server");
//        server.setUrl("http://localhost:8080");
//        servers.add(server);

        return servers.stream()
                .map(serve -> new Server().description(serve.name).url(String.format("%s%s", serve.url, CONTEXT_PATH)))
                .toList();
    }
}