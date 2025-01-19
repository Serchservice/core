package com.serch.server.admin.services.account.services;

import com.serch.server.admin.enums.ActivityMode;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.services.team.responses.AdminActivityResponse;
import com.serch.server.models.auth.User;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing admin activities within the Serch organization.
 * This interface provides methods to retrieve and create activity logs for admins,
 * allowing for tracking of user actions and system interactions.
 * Implementations of this interface encapsulate the business logic related to
 * admin activities, ensuring efficient retrieval and creation of activity records.
 */
public interface AdminActivityService {

    /**
     * Retrieves a list of activity logs for a specific admin identified by their unique ID.
     * This method provides insights into the actions performed by the admin,
     * facilitating monitoring and auditing of admin activities.
     *
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @param id The unique identifier (UUID) of the admin whose activities are to be retrieved.
     * @return A list of {@link AdminActivityResponse} containing the activity logs associated
     * with the specified admin.
     */
    List<AdminActivityResponse> activities(UUID id, Integer page, Integer size);

    /**
     * Retrieves a list of activity logs for all admins within the organization.
     * This method aggregates the activities of all admins, providing a comprehensive view
     * of admin actions across the system.
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     *
     * @return A list of {@link AdminActivityResponse} containing activity logs for all admins.
     */
    List<AdminActivityResponse> activities(Integer page, Integer size);

    /**
     * Creates a new activity log entry associated with a specific admin.
     * This method records an action performed by an admin, capturing the
     * activity mode, associated response, and the admin's details for auditing purposes.
     *
     * @param mode The {@link ActivityMode} indicating the type of activity being logged.
     * @param associated A string representing any associated response relevant to the activity.
     * @param account A string representing the account related to the activity.
     * @param admin The {@link Admin} instance representing the admin who performed the activity.
     */
    void create(ActivityMode mode, String associated, String account, Admin admin);

    /**
     * Creates a new activity log entry associated with a specific user.
     * This method records an action performed by a user, capturing the
     * activity mode, associated response, and the user's details for auditing purposes.
     *
     * @param mode The {@link ActivityMode} indicating the type of activity being logged.
     * @param associated A string representing any associated response relevant to the activity.
     * @param account A string representing the account related to the activity.
     * @param user The {@link User} instance representing the user who performed the activity.
     */
    void create(ActivityMode mode, String associated, String account, User user);
}