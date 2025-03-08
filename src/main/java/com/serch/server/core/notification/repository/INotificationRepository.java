package com.serch.server.core.notification.repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface for accessing notification-related response, such as tokens, names, and avatars.
 */
public interface INotificationRepository {
    /**
     * Retrieves the token associated with a specific user or entity by ID.
     *
     * @param id The unique identifier of the user or entity.
     * @return The token associated with the given ID.
     */
    Optional<String> getToken(String id);

    /**
     * Retrieves the role associated with a specific user or entity by ID.
     *
     * @param id The unique identifier of the user or entity.
     * @return The token associated with the given ID.
     */
    default String getRole(String id) {
        return "";
    }

    /**
     * Retrieves the business token associated with a specific business by UUID.
     *
     * @param id The unique identifier (UUID) of the business.
     * @return The business token associated with the given UUID.
     */
    default Optional<String> getBusinessToken(UUID id) {
        return Optional.empty();
    }

    /**
     * Retrieves the name associated with a specific user or entity by ID.
     *
     * @param id The unique identifier of the user or entity.
     * @return The name associated with the given ID.
     */
    String getName(String id);

    /**
     * Retrieves the avatar URL or path associated with a specific user or entity by ID.
     *
     * @param id The unique identifier of the user or entity.
     * @return The avatar URL or path associated with the given ID.
     */
    String getAvatar(String id);
}