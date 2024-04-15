package com.serch.server.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

/**
 * The WebSocketConfiguration class configures WebSocket communication for the application.
 * It is annotated with @Configuration to indicate that it defines application beans.
 * Additionally, it enables WebSocket message broker functionality.
 *
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    /**
     * Registers STOMP endpoints for WebSocket communication.
     *
     * @param registry The StompEndpointRegistry for registering endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws:serch.app")
                .setAllowedOrigins("*", "http://localhost:3000")
                .setAllowedOriginPatterns("/**")
                .withSockJS();
    }

    /**
     * Configures message converters for WebSocket communication.
     *
     * @param messageConverters The list of message converters to configure.
     * @return false to indicate that default message converters should not be overridden.
     */
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }

    /**
     * Configures the message broker for WebSocket communication.
     *
     * @param registry The MessageBrokerRegistry for configuring the message broker.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/serch");
        registry.setApplicationDestinationPrefixes("/serch.app");
        registry.setUserDestinationPrefix("/serch");
    }
}