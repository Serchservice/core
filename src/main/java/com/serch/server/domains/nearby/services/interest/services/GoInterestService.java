package com.serch.server.domains.nearby.services.interest.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.bases.BaseLocation;
import com.serch.server.domains.nearby.models.go.interest.GoInterest;
import com.serch.server.domains.nearby.models.go.interest.GoInterestCategory;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.models.go.user.GoUserInterest;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestCategoryResponse;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestResponse;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestUpdateResponse;

import java.util.List;

/**
 * This interface defines the contract for managing user interests.
 * It provides methods for interacting with user interests, including
 * creating, retrieving, and managing interest categories.
 */
public interface GoInterestService {
    /**
     * Adds to the user's interests mixed with `taken` and `reserved` options.
     *
     * @param interestIds A list of IDs representing the user's selected interests.
     * @return An {@link ApiResponse} containing {@link GoInterestUpdateResponse} objects, or an error message.
     */
    ApiResponse<GoInterestUpdateResponse> add(List<Long> interestIds);

    /**
     * Deletes from the user's interests mixed with `taken` and `reserved` options.
     *
     * @param interestIds A list of IDs representing the user's selected interests.
     * @return An {@link ApiResponse} containing {@link GoInterestUpdateResponse} objects, or an error message.
     */
    ApiResponse<GoInterestUpdateResponse> delete(List<Long> interestIds);

    /**
     * Gets the user's interests mixed with `taken` and `reserved` options.
     *
     * @return An {@link ApiResponse} containing {@link GoInterestUpdateResponse} objects, or an error message.
     */
    ApiResponse<GoInterestUpdateResponse> getUpdate();

    /**
     * Retrieves the user's interests.
     *
     * @return An {@link ApiResponse} containing a list of {@link GoInterestResponse} objects, or an error message.
     */
    ApiResponse<List<GoInterestResponse>> get();

    /**
     * Retrieves all available interests.
     *
     * @param location The {@link BaseLocation} of the searching user's address.
     *
     * @return An {@link ApiResponse} containing a list of {@link GoInterestResponse} objects, or an error message.
     */
    ApiResponse<List<GoInterestResponse>> getAll(BaseLocation location);

    /**
     * Retrieves a specific interest in its ID.
     *
     * @param location The {@link BaseLocation} of the searching user's address.
     * @param id The ID of the interest to retrieve.
     * @return An {@link ApiResponse} containing the {@link GoInterestResponse} object, or an error message.
     */
    ApiResponse<GoInterestResponse> get(Long id, BaseLocation location);

    /**
     * Retrieves all user interest categories.
     *
     * @return An {@link ApiResponse} containing a list of {@link GoInterestCategoryResponse} objects, or an error message.
     */
    ApiResponse<List<GoInterestCategoryResponse>> getCategory();

    /**
     * Retrieves all available interest categories.
     *
     * @param location The {@link BaseLocation} of the searching user's address.
     *
     * @return An {@link ApiResponse} containing a list of {@link GoInterestCategoryResponse} objects, or an error message.
     */
    ApiResponse<List<GoInterestCategoryResponse>> getAllCategories(BaseLocation location);

    /**
     * Retrieves a specific interest category by its ID.
     *
     * @param location The {@link BaseLocation} of the searching user's address.
     * @param id The ID of the interest category to retrieve.
     * @return An {@link ApiResponse} containing the {@link GoInterestCategoryResponse} object, or an error message.
     */
    ApiResponse<GoInterestCategoryResponse> getCategory(Long id, BaseLocation location);

    /**
     * Prepares a {@link GoInterest} object for persistence.
     *
     * @param radius The search radius of the searching user.
     * @param lng The longitude of the searching user's address.
     * @param lat The latitude of the searching user's address.
     * @param interest The {@link GoInterest} object to prepare.
     * @return The prepared {@link GoInterestResponse} object.
     */
    GoInterestResponse prepare(GoInterest interest, double lng, double lat, double radius);

    /**
     * Prepares a {@link GoInterestCategory} object for persistence.
     *
     * @param interests The list of {@link List<GoInterest>} to map.
     * @param radius The search radius of the searching user.
     * @param lng The longitude of the searching user's address.
     * @param lat The latitude of the searching user's address.
     * @param category The {@link GoInterestCategory} object to prepare.
     * @return The prepared {@link GoInterestCategoryResponse} object.
     */
    GoInterestCategoryResponse prepare(GoInterestCategory category, List<GoInterest> interests, double lng, double lat, double radius);

    /**
     * Updates or creates a user interest.
     *
     * <p>If a user interest with the given ID exists, it will be updated.
     * Otherwise, a new user interest will be created.</p>
     *
     * @param user The {@link GoUser} who owns the interest.
     * @param id The ID of the user interest to update or create.
     * @return An {@link GoUserInterest} containing the updated or created GoUserInterest object.
     */
    GoUserInterest put(Long id, GoUser user);
}