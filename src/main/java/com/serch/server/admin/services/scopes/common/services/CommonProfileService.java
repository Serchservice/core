package com.serch.server.admin.services.scopes.common.services;

import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.models.auth.User;

import java.util.UUID;

/**
 * Service interface for handling common profile-related operations,
 * such as fetching profiles based on transaction ID, UUID, or user information.
 */
public interface CommonProfileService {

    /**
     * Retrieves a common profile based on a transaction ID.
     *
     * @param id The transaction ID associated with the profile.
     * @return A {@link CommonProfileResponse} containing the profile data.
     */
    CommonProfileResponse fromTransaction(String id);

    /**
     * Retrieves a common profile based on a UUID.
     *
     * @param id The UUID associated with the profile.
     * @return A {@link CommonProfileResponse} containing the profile data.
     */
    CommonProfileResponse fromId(UUID id);

    /**
     * Retrieves a common profile based on user information.
     *
     * @param user The {@link User} object associated with the profile.
     * @return A {@link CommonProfileResponse} containing the profile data.
     */
    CommonProfileResponse fromUser(User user);

    /**
     * Retrieves a common profile based on a string representation of an ID.
     *
     * @param id The string representation of the ID associated with the profile.
     * @return A {@link CommonProfileResponse} containing the profile data.
     */
    CommonProfileResponse fromId(String id);
}