package com.serch.server.configurations;

import com.serch.server.core.validator.AllowedOriginValidatorService;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SwaggerConfiguration {
    private final AllowedOriginValidatorService service;

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
                        .version("1.4.0")
                        .summary("This contains the server implementations for Serch platform")
                        .description("""
                                This API supports four authentication methods:
                                
                                 1. **Signature Authentication**: Use the `X-Serch-Signed` header to verify that the connection is coming from an authorized Serch user. This is **compulsory** for any API request.
                                 2. **Bearer Authentication**: Optionally, use a JWT token in the `Authorization` header as `Bearer <token>`.
                                 3. **Drive Authentication**: Optionally, use the `X-Serch-Drive-Api-Key` and `X-Serch-Drive-Secret-Key` headers for Drive authentication.
                                 4. **Guest Authentication**: Optionally, use the `X-Serch-Guest-Api-Key`, `X-Serch-Guest-Secret-Key` and `Guest-Authorization` headers for Guest access.
                                
                                 Choose **Signature Authentication** for all requests, and optionally select one of the other methods for additional access control.
                                """
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
                        .addList("Signature Authentication")
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

        // Add Signature API Key scheme
        components.addSecuritySchemes(
                "Signature Authentication",
                getApiKeyScheme("X-Serch-Signed", "API key for Signature Authentication")
        );

        // Add Drive API Key scheme
        components.addSecuritySchemes(
                "Drive Authentication",
                getApiKeyScheme("X-Serch-Drive-Api-Key", "API key for Drive App authentication")
        );
        components.addSecuritySchemes(
                "Drive Secret Key",
                getApiKeyScheme("X-Serch-Drive-Secret-Key", "Secret key for Drive App authentication")
        );

        // Add Guest API Key scheme
        components.addSecuritySchemes(
                "Guest Authentication",
                getApiKeyScheme("X-Serch-Guest-Api-Key", "API key for Guest App authentication")
        );
        components.addSecuritySchemes(
                "Guest Secret Key",
                getApiKeyScheme("X-Serch-Guest-Secret-Key", "Secret key for Guest App authentication")
        );

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

    private SecurityScheme getApiKeyScheme(String name, String description) {
        SecurityScheme scheme = new SecurityScheme();
        scheme.setType(SecurityScheme.Type.APIKEY);
        scheme.setIn(SecurityScheme.In.HEADER);
        scheme.setName(name);
        scheme.setDescription(description);

        return scheme;
    }

    @Data
    private static class ApiServer {
        private String name;
        private String url;
    }

    private List<Server> getServers() {
        List<ApiServer> servers = new ArrayList<>();

        if(service.isDevelopment()) {
            servers.add(getServer("Development Server", "http://localhost:8080"));
        } else {
            servers.add(getServer("Production Server", "https://api.serchservice.com"));
            servers.add(getServer("Sandbox Server", "https://sandbox.serchservice.com"));
        }

        return servers.stream()
                .map(serve -> new Server().description(serve.name).url(String.format("%s%s", serve.url, CONTEXT_PATH)))
                .toList();
    }

    private ApiServer getServer(String name, String url) {
        ApiServer server = new ApiServer();
        server.setName(name);
        server.setUrl(url);

        return server;
    }
}