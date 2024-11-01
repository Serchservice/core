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
                        .version("1.0.9")
                        .summary("This contains the server implementations for Serch platform")
                        .description("""
                                This API supports three authentication methods:\s

                                1. **Bearer Authentication**: Use a JWT token in the `Authorization` header as `Bearer <token>`.
                                2. **Drive Authentication**: Use `X-Serch-Drive-Api-Key` and `X-Serch-Drive-Secret-Key` headers for Drive authentication.
                                3. **Guest Authentication**: Use `X-Serch-Guest-Api-Key` and `X-Serch-Guest-Secret-Key` headers for Guest access.

                                Choose one of these methods to authenticate requests."""
                        )
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
                .components(getSecurityComponent())
                .servers(getServers())
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication")
                        .addList("Drive Authentication")
                        .addList("Drive Secret Key")
                        .addList("Guest Authentication")
                        .addList("Guest Secret Key"));
    }

    private Components getSecurityComponent() {
        Components components = new Components();
        // Add Bearer Authentication scheme
        components.addSecuritySchemes("Bearer Authentication", getBearerScheme());

        // Add Drive API Key scheme
        components.addSecuritySchemes("Drive Authentication", getDriveApiKeyScheme());
        components.addSecuritySchemes("Drive Secret Key", getDriveSecretKeyScheme());

        // Add Guest API Key scheme
        components.addSecuritySchemes("Guest Authentication", getGuestApiKeyScheme());
        components.addSecuritySchemes("Guest Secret Key", getGuestSecretKeyScheme());

        return components;
    }

    private SecurityScheme getBearerScheme() {
        SecurityScheme scheme = new SecurityScheme();
        scheme.setType(SecurityScheme.Type.HTTP);
        scheme.setScheme("Bearer");
        scheme.setBearerFormat("JWT");
        scheme.setDescription("Using JWT to authenticate the signed-in user");

        return scheme;
    }

    private SecurityScheme getDriveApiKeyScheme() {
        SecurityScheme scheme = new SecurityScheme();
        scheme.setType(SecurityScheme.Type.APIKEY);
        scheme.setIn(SecurityScheme.In.HEADER);
        scheme.setName("X-Serch-Drive-Api-Key");
        scheme.setDescription("API key for Drive App authentication");

        return scheme;
    }

    private SecurityScheme getDriveSecretKeyScheme() {
        SecurityScheme scheme = new SecurityScheme();
        scheme.setType(SecurityScheme.Type.APIKEY);
        scheme.setIn(SecurityScheme.In.HEADER);
        scheme.setName("X-Serch-Drive-Secret-Key");
        scheme.setDescription("Secret key for Drive App authentication");

        return scheme;
    }

    private SecurityScheme getGuestApiKeyScheme() {
        SecurityScheme scheme = new SecurityScheme();
        scheme.setType(SecurityScheme.Type.APIKEY);
        scheme.setIn(SecurityScheme.In.HEADER);
        scheme.setName("X-Serch-Guest-Api-Key");
        scheme.setDescription("API key for Guest App authentication");

        return scheme;
    }

    private SecurityScheme getGuestSecretKeyScheme() {
        SecurityScheme scheme = new SecurityScheme();
        scheme.setType(SecurityScheme.Type.APIKEY);
        scheme.setIn(SecurityScheme.In.HEADER);
        scheme.setName("X-Serch-Guest-Secret-Key");
        scheme.setDescription("Secret key for Guest App authentication");

        return scheme;
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

        return servers.stream()
                .map(serve -> new Server().description(serve.name).url(String.format("%s%s", serve.url, CONTEXT_PATH)))
                .toList();
    }
}