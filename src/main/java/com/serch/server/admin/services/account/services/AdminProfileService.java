package com.serch.server.admin.services.account.services;

import com.serch.server.admin.models.Admin;
import com.serch.server.admin.services.account.requests.AdminProfileUpdateRequest;
import com.serch.server.admin.services.account.responses.AdminProfileResponse;
import com.serch.server.admin.services.account.responses.AdminResponse;
import com.serch.server.admin.services.team.responses.AdminTeamResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.storage.requests.FileUploadRequest;

/**
 * Service interface for managing admin profile operations within the Serch organization.
 * This interface provides methods to retrieve and update admin profile information,
 * handle avatar uploads, manage team data, and adjust user timezones.
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
     * {@link Admin} data. This method formats the admin's information for
     * presentation, ensuring it adheres to the required structure for responses.
     *
     * @param admin The {@link Admin} data to be prepared and formatted.
     * @return {@link AdminResponse} containing the formatted admin information.
     */
    AdminResponse prepare(Admin admin);

    /**
     * Updates the profile information of the currently logged-in admin based
     * on the provided update request data. This method ensures that the
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
     * Constructs the admin profile data based on the provided {@link Admin}
     * object. This method aggregates the relevant details into an
     * {@link AdminProfileResponse} for easy access and management.
     *
     * @param admin The {@link Admin} whose profile information is to be fetched.
     * @return {@link AdminProfileResponse} containing the compiled profile data
     * for the specified admin.
     */
    AdminProfileResponse profile(Admin admin);

    /**
     * Constructs the team data associated with the specified {@link Admin}.
     * This method provides insights into the admin's team, including team
     * members and roles within the team structure.
     *
     * @param admin The {@link Admin} whose team information is to be fetched.
     * @return {@link AdminTeamResponse} containing the team data associated
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