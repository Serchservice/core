package com.serch.server.core.validator;

/**
 * The {@code EndpointValidatorService} interface defines methods for validating
 * access permissions for various endpoints in the application.
 * Implementations of this interface should provide the logic to determine
 * if a specific path is permitted for different user roles, such as drive
 * users and guests.
 */
public interface EndpointValidatorService {
    /**
     * Checks if the specified path is permitted for drive users.
     *
     * @param path the path to validate
     * @return {@code true} if the path is permitted for drive users;
     *         {@code false} otherwise
     */
    boolean isDrivePermitted(String path);

    /**
     * Checks if the specified path is permitted for guest users.
     *
     * @param path the path to validate
     * @return {@code true} if the path is permitted for guest users;
     *         {@code false} otherwise
     */
    boolean isGuestPermitted(String path);

    /**
     * Checks if the specified path is permitted for swagger access.
     *
     * @param path the path to validate
     * @return {@code true} if the path is permitted for swagger access;
     *         {@code false} otherwise
     */
    boolean isSwaggerPermitted(String path);

    /**
     * Checks if the specified path is permitted for websocket access.
     *
     * @param path the path to validate
     * @return {@code true} if the path is permitted for websocket access;
     *         {@code false} otherwise
     */
    boolean isSocketPermitted(String path);

    /**
     * Checks if the specified path is permitted for authenticated nearby/drive - go access only.
     *
     * @param path the path to validate
     * @return {@code true} if the path is permitted for authenticated nearby/drive - go access only;
     *         {@code false} otherwise
     */
    boolean isGoAuthenticatedOnly(String path);

    /**
     * Checks if the specified path is permitted for nearby/drive - go access only.
     *
     * @param path the path to validate
     * @return {@code true} if the path is permitted for nearby/drive - go access only;
     *         {@code false} otherwise
     */
    boolean isGoAllowedOnly(String path);
}