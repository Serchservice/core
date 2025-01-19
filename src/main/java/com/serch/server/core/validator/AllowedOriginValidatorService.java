package com.serch.server.core.validator;

import java.util.List;

/**
 * Service interface for validating origins used in the server and WebSocket connections.
 * Provides methods to retrieve allowed origins and origin patterns.
 */
public interface AllowedOriginValidatorService {
    /**
     * Checks if the current environment is a dev environment.
     *
     * @return true or false.
     */
    boolean isDevelopment();

    /**
     * Checks if the current environment is a sandbox environment.
     *
     * @return true or false.
     */
    boolean isSandbox();

    /**
     * Retrieves the list of allowed WebSocket origins.
     *
     * @return an array of allowed WebSocket origin strings.
     */
    String[] getWebSocketOrigins();

    /**
     * Retrieves the list of allowed WebSocket origin patterns.
     * These patterns can include wildcards or regular expressions.
     *
     * @return an array of allowed WebSocket origin patterns.
     */
    String[] getWebSocketOriginPatterns();

    /**
     * Retrieves the list of allowed server origins.
     *
     * @return a list of allowed server origin strings.
     */
    List<String> getServerOrigins();

    /**
     * Retrieves the list of allowed server origin patterns.
     * These patterns can include wildcards or regular expressions.
     *
     * @return a list of allowed server origin patterns.
     */
    List<String> getServerOriginPatterns();
}