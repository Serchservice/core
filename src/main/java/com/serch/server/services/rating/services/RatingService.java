package com.serch.server.services.rating.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.rating.Rating;
import com.serch.server.services.rating.requests.RateAppRequest;
import com.serch.server.services.rating.requests.RateRequest;
import com.serch.server.services.rating.responses.RatingChartResponse;
import com.serch.server.services.rating.responses.RatingResponse;

import java.util.List;

/**
 * Interface defining methods for managing ratings, including rating providers,
 * sharing ratings, rating the application, viewing ratings, and retrieving
 * statistical data about ratings.
 */
public interface RatingService {

    /**
     * Rates a provider, call, schedule, or trip based on the provided details.
     *
     * @param request The rating request containing necessary information.
     * @return A response indicating the status of the rating operation.
     *
     * @see ApiResponse
     * @see RateRequest
     */
    ApiResponse<String> rate(RateRequest request);

    /**
     * Rates the application based on the provided rating and account.
     * Updates the existing rating if one exists for the account.
     *
     * @param request The request containing the rating and account information.
     * @return A response indicating the status of the rating operation.
     *
     * @see RateAppRequest
     * @see ApiResponse
     * @see RatingResponse
     */
    ApiResponse<RatingResponse> rate(RateAppRequest request);

    /**
     * Retrieves all ratings received by the current user.
     *
     * @return A response containing a list of rating responses.
     *
     * @see RatingResponse
     * @see ApiResponse
     */
    ApiResponse<List<RatingResponse>> view();

    /**
     * Retrieves good ratings received by the current user.
     *
     * @param id The id of the user to find its rating
     *
     * @return A response containing a list of good rating responses.
     *
     * @see RatingResponse
     * @see ApiResponse
     */
    ApiResponse<List<RatingResponse>> good(String id);

    /**
     * Retrieves bad ratings received by the current user.
     *
     * @param id The id of the user to find its rating
     *
     * @return A response containing a list of bad rating responses.
     *
     * @see RatingResponse
     * @see ApiResponse
     */
    ApiResponse<List<RatingResponse>> bad(String id);

    /**
     * Retrieves statistical data about ratings for the current user.
     *
     * @param id The id of the user to find its rating
     * @return A response containing a list of rating chart responses.
     *
     * @see RatingChartResponse
     * @see ApiResponse
     */
    ApiResponse<List<RatingChartResponse>> chart(String id);

    /**
     * Retrieves the application rating for a specified account.
     * If no account is provided, it retrieves the rating for the current user.
     *
     * @param id The ID of the account for which the rating is requested.
     * @return A response containing the application rating.
     *
     * @see ApiResponse
     * @see RatingResponse
     */
    ApiResponse<RatingResponse> app(String id);

    /**
     * @param ratings The list of ratings
     *
     * @return {@link ApiResponse} of list of {@link RatingResponse}
     */
    ApiResponse<List<RatingResponse>> ratings(List<Rating> ratings);
}