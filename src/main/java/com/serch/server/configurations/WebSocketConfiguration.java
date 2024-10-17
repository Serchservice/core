package com.serch.server.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serch.server.utils.ServerUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Arrays;
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
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketConfiguration.class);

    private final WebSocketInterceptor interceptor;
    private final ObjectMapper objectMapper;

    /**
     * Register STOMP endpoints for WebSocket communication.
     *
     * @param registry The StompEndpointRegistry for registering endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] origins = ServerUtil.ALLOWED_ORIGINS.toArray(new String[0]);
        String[] patterns = ServerUtil.ALLOWED_ORIGIN_PATTERNS.toArray(new String[0]);

        registry.addEndpoint("/ws:chat", "/ws:serch", "/ws:trip", "/ws", "/ws:call")
                .setAllowedOrigins(origins)
                .setAllowedOriginPatterns(patterns)
                .withSockJS();

        log.info(String.format("SERCH::: WEBSOCKET | Allowed Origins | %s", Arrays.toString(origins)));
        log.info(String.format("SERCH::: WEBSOCKET | Allowed Origin Patterns | %s", Arrays.toString(patterns)));
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
        converter.setObjectMapper(objectMapper);
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
        registry.enableSimpleBroker("/platform", "/topic");
        registry.setApplicationDestinationPrefixes("");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(interceptor);
    }
}