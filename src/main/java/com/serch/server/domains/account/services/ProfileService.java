package com.serch.server.domains.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.domains.account.requests.RequestCreateProfile;
import com.serch.server.domains.account.requests.UpdateProfileRequest;
import com.serch.server.domains.account.responses.MoreProfileData;
import com.serch.server.domains.account.responses.ProfileResponse;
import com.serch.server.domains.auth.requests.RequestPhoneInformation;
import com.serch.server.domains.auth.requests.RequestProfile;

/**
 * Service interface for managing user profiles, including creation, updating, and retrieval.
 *
 * @see com.serch.server.domains.account.services.implementations.ProfileImplementation
 */
public interface ProfileService {
    /**
     * Creates a user profile based on the provided request.
     *
     * @param request The request containing user profile information.
     * @return An ApiResponse containing the created profile.
     *
     * @see ApiResponse
     * @see RequestCreateProfile
     */
    ApiResponse<Profile> createProfile(RequestCreateProfile request);

    /**
     * Creates a provider profile based on incomplete information and a user.
     *
     * @param incomplete Incomplete information about the provider.
     * @param user       The user for whom the profile is being created.
     * @return An ApiResponse containing the created profile.
     *
     * @see ApiResponse
     * @see Incomplete
     * @see User
     */
    ApiResponse<Profile> createProviderProfile(Incomplete incomplete, User user);

    /**
     * Creates a user profile based on provided profile information and user details.
     *
     * @param request  The request containing user profile information.
     * @param user     The user for whom the profile is being created.
     * @param referral The user who referred the new user.
     * @return An ApiResponse containing the created profile.
     *
     * @see RequestProfile
     * @see User
     * @see ApiResponse
     * @see Profile
     */
    ApiResponse<Profile> createUserProfile(RequestProfile request, User user, User referral);

    /**
     * Retrieves the user's profile.
     *
     * @return An ApiResponse containing the user's profile.
     *
     * @see ApiResponse
     * @see ProfileResponse
     */
    ApiResponse<ProfileResponse> profile();

    /**
     * Retrieves the user's profile with the provided ID.
     *
     * @param profile The profile of the user/provider
     * @return An ApiResponse containing the user's profile.
     *
     * @see Profile
     * @see ProfileResponse
     */
    ProfileResponse profile(Profile profile);

    /**
     * Updates the user's profile based on the provided request.
     *
     * @param request The request containing updated profile information.
     * @return An ApiResponse indicating the success of the update.
     *
     * @see ApiResponse
     * @see UpdateProfileRequest
     * @see ProfileResponse
     */
    ApiResponse<ProfileResponse> update(UpdateProfileRequest request);

    /**
     * Update the phone information of the logged-in user.
     *
     * @param request The {@link RequestPhoneInformation} for the update
     * @param user The {@link User} making the update
     */
    void updatePhoneInformation(RequestPhoneInformation request, User user);

    /**
     * This prepares more profile information of the logged-in user
     *
     * @param user The {@link User} whose information is being prepared
     *
     * @return MoreInformation about the logged-in user
     *
     * @see MoreProfileData
     */
    MoreProfileData moreInformation(User user);
}