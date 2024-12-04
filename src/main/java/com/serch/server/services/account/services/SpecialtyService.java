package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.models.account.Specialty;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.services.account.responses.SpecialtyResponse;

import java.util.List;

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
     * @param specialty The specialty keyword to add.
     * @return An ApiResponse containing information about the added specialty.
     *
     * @see ApiResponse
     * @see SpecialtyResponse
     */
    ApiResponse<SpecialtyResponse> add(String specialty);

    /**
     * Deletes a specialty from a user's profile.
     *
     * @param id The ID of the specialty to delete.
     * @return An ApiResponse indicating the success of the deletion.
     *
     * @see ApiResponse
     */
    ApiResponse<String> delete(Long id);

    /**
     * Prepares the specialty response for the given specialty
     *
     * @param specialty The specialty to prepare the response from
     *
     * @return {@link SpecialtyResponse}
     */
    SpecialtyResponse response(Specialty specialty);

    /**
     * Search for a skill or category
     *
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @param query The skill or category being searched for
     *
     * @return {@link ApiResponse} list of {@link SpecialtyResponse}
     */
    ApiResponse<List<SpecialtyResponse>> search(String query, Integer page, Integer size);

    /**
     * Get the list of specialties
     *
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     *
     * @return {@link ApiResponse} list of String
     */
    ApiResponse<List<String>> specialties(Integer page, Integer size);
}