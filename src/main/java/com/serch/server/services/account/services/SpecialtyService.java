package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.services.company.responses.SpecialtyKeywordResponse;

/**
 * Service interface for managing specialties and specialty keywords.
 *
 * @see com.serch.server.services.account.services.implementations.SpecialtyImplementation
 */
public interface SpecialtyService {

    /**
     * Saves incomplete specialties for a profile.
     *
     * @param incomplete The incomplete profile information containing specialties.
     * @param profile    The profile to which the specialties belong.
     *
     * @see Incomplete
     * @see Profile
     */
    void createSpecialties(Incomplete incomplete, Profile profile);

    /**
     * Adds a specialty keyword to a user's profile.
     *
     * @param id The ID of the specialty keyword to add.
     * @return An ApiResponse containing information about the added specialty.
     *
     * @see ApiResponse
     * @see SpecialtyKeywordResponse
     */
    ApiResponse<SpecialtyKeywordResponse> add(Long id);

    /**
     * Deletes a specialty from a user's profile.
     *
     * @param id The ID of the specialty to delete.
     * @return An ApiResponse indicating the success of the deletion.
     *
     * @see ApiResponse
     */
    ApiResponse<String> delete(Long id);
}