package com.serch.server.core.notification.repository;

import java.util.UUID;

/**
 * Interface for accessing notification-related data, such as tokens, names, and avatars.
 */
public interface INotificationRepository {

    /**
     * Retrieves the token associated with a specific user or entity by ID.
     *
     * @param id The unique identifier of the user or entity.
     * @return The token associated with the given ID.
     */
    String getToken(String id);

    /**
     * Retrieves the role associated with a specific user or entity by ID.
     *
     * @param id The unique identifier of the user or entity.
     * @return The token associated with the given ID.
     */
    String getRole(String id);

    /**
     * Retrieves the business token associated with a specific business by UUID.
     *
     * @param id The unique identifier (UUID) of the business.
     * @return The business token associated with the given UUID.
     */
    String getBusinessToken(UUID id);

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