package com.serch.server.admin.services.account.services;

import com.serch.server.admin.models.Admin;
import com.serch.server.admin.services.account.requests.AdminProfileUpdateRequest;
import com.serch.server.admin.services.account.responses.AdminProfileResponse;
import com.serch.server.admin.services.account.responses.AdminResponse;
import com.serch.server.admin.services.account.responses.AdminTeamResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.storage.requests.FileUploadRequest;

public interface AdminProfileService {
    /**
     * Get the {@link Admin} response of the logged in admin
     *
     * @return {@link ApiResponse} of logged in {@link AdminResponse}
     */
    ApiResponse<AdminResponse> get();

    /**
     * Prepare an admin data from the admin information passed
     *
     * @param admin The {@link Admin} data
     * @return {@link AdminResponse} data of formatted information
     */
    AdminResponse prepare(Admin admin);

    /**
     * Update the profile information of the logged in admin
     *
     * @param request The {@link AdminProfileUpdateRequest} request data
     *
     * @return {@link ApiResponse} of logged in {@link AdminResponse}
     */
    ApiResponse<AdminResponse> update(AdminProfileUpdateRequest request);

    /**
     * Upload avatar for the logged in admin
     *
     * @param request The {@link FileUploadRequest} request data
     *
     * @return {@link ApiResponse} of logged in {@link AdminResponse}
     */
    ApiResponse<AdminResponse> uploadAvatar(FileUploadRequest request);

    /**
     * Build the admin profile data
     *
     * @param admin The {@link Admin} whose profile is to be fetched
     *
     * @return {@link AdminProfileResponse}
     */
    AdminProfileResponse profile(Admin admin);

    /**
     * Build the admin team data
     *
     * @param admin The {@link Admin} whose profile is to be fetched
     *
     * @return {@link AdminTeamResponse}
     */
    AdminTeamResponse team(Admin admin);

    /**
     * Update the timezone of the logged-in user
     *
     * @param timezone The new timezone
     *
     * @return {@link ApiResponse} of Success or failure
     */
    ApiResponse<String> updateTimezone(String timezone);
}