package com.serch.server.admin.services.account.services;

import com.serch.server.admin.models.Admin;
import com.serch.server.admin.services.account.requests.AdminProfileUpdateRequest;
import com.serch.server.admin.services.account.responses.AdminProfileResponse;
import com.serch.server.admin.services.account.responses.AdminResponse;
import com.serch.server.admin.services.responses.AccountScopeDetailResponse;
import com.serch.server.admin.services.team.responses.AdminTeamResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.storage.requests.FileUploadRequest;
import com.serch.server.models.auth.User;

import java.time.ZonedDateTime;

/**
 * Service interface for managing admin profile operations within the Serch organization.
 * This interface provides methods to retrieve and update admin profile information,
 * handle avatar uploads, manage team response, and adjust user timezones.
 * Implementations of this interface encapsulate the business logic associated with
 * admin profile management.
 */
public interface AdminProfileService {

    /**
     * Retrieves the profile information of the currently logged-in admin.
     * This method provides the necessary details about the admin's account,
     * including roles and permissions.
     *
     * @return {@link ApiResponse} containing the {@link AdminResponse}
     * of the logged-in admin, providing detailed profile information.
     */
    ApiResponse<AdminResponse> get();

    /**
     * Prepares an {@link AdminResponse} object based on the provided
     * {@link Admin} response. This method formats the admin's information for
     * presentation, ensuring it adheres to the required structure for responses.
     *
     * @param admin The {@link Admin} response to be prepared and formatted.
     * @return {@link AdminResponse} containing the formatted admin information.
     */
    AdminResponse prepare(Admin admin);

    /**
     * Updates the profile information of the currently logged-in admin based
     * on the provided update request response. This method ensures that the
     * admin's profile is current and accurately reflects their information.
     *
     * @param request The {@link AdminProfileUpdateRequest} containing the
     * updated information for the admin profile.
     * @return {@link ApiResponse} containing the updated {@link AdminResponse}
     * of the logged-in admin.
     */
    ApiResponse<AdminResponse> update(AdminProfileUpdateRequest request);

    /**
     * Uploads a new avatar for the currently logged-in admin. This method
     * handles the file upload request and associates the uploaded avatar
     * with the admin's profile.
     *
     * @param request The {@link FileUploadRequest} containing the details
     * of the file to be uploaded as the admin's avatar.
     * @return {@link ApiResponse} containing the updated {@link AdminResponse}
     * of the logged-in admin after the avatar upload.
     */
    ApiResponse<AdminResponse> uploadAvatar(FileUploadRequest request);

    /**
     * Constructs the admin profile response based on the provided {@link Admin}
     * object. This method aggregates the relevant details into an
     * {@link AdminProfileResponse} for easy access and management.
     *
     * @param admin The {@link Admin} whose profile information is to be fetched.
     * @return {@link AdminProfileResponse} containing the compiled profile response
     * for the specified admin.
     */
    AdminProfileResponse profile(Admin admin);

    /**
     * Updates the profile details of an account scope with the provided user information
     * and timestamps for creation and last update.
     *
     * @param updatedAt The timestamp indicating when the profile was last updated.
     * @param createdAt The timestamp indicating when the profile was initially created.
     * @param user The {@link User} entity containing the user's details to be updated in the profile.
     * @param response The response object of type {@link AccountScopeDetailResponse}
     *                 or its subclass, which will be updated with the provided details.
     * @param <T> A type that extends {@link AccountScopeDetailResponse}.
     * @return The updated response object containing the modified profile details.
     */
    <T extends AccountScopeDetailResponse> T updateProfile(ZonedDateTime updatedAt, ZonedDateTime createdAt, User user, T response);

    /**
     * Constructs the team response associated with the specified {@link Admin}.
     * This method provides insights into the admin's team, including team
     * members and roles within the team structure.
     *
     * @param admin The {@link Admin} whose team information is to be fetched.
     * @return {@link AdminTeamResponse} containing the team response associated
     * with the specified admin.
     */
    AdminTeamResponse team(Admin admin);

    /**
     * Updates the timezone setting for the currently logged-in user. This
     * method allows the admin to set their preferred timezone, ensuring
     * that timestamps and scheduling are appropriately aligned with their
     * local time.
     *
     * @param timezone The new timezone identifier to be set for the admin.
     * @return {@link ApiResponse} indicating success or failure of the
     * timezone update operation.
     */
    ApiResponse<String> updateTimezone(String timezone);
}