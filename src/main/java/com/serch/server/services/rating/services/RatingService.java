package com.serch.server.services.rating.services;

import com.serch.server.bases.ApiResponse;
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
     * Shares a rating for a trip, splitting the rating into two separate ones.
     *
     * @param request The rating request containing necessary information.
     * @return A response indicating the status of the sharing operation.
     *
     * @see RateRequest
     * @see ApiResponse
     */
    ApiResponse<String> share(RateRequest request);

    /**
     * Rates the application based on the provided rating and account.
     * Updates the existing rating if one exists for the account.
     *
     * @param request The request containing the rating and account information.
     * @return A response indicating the status of the rating operation.
     *
     * @see RateAppRequest
     * @see ApiResponse
     */
    ApiResponse<Double> rate(RateAppRequest request);

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
     * @return A response containing a list of good rating responses.
     *
     * @see RatingResponse
     * @see ApiResponse
     */
    ApiResponse<List<RatingResponse>> good();

    /**
     * Retrieves bad ratings received by the current user.
     *
     * @return A response containing a list of bad rating responses.
     *
     * @see RatingResponse
     * @see ApiResponse
     */
    ApiResponse<List<RatingResponse>> bad();

    /**
     * Retrieves statistical data about ratings for the current user.
     *
     * @return A response containing a list of rating chart responses.
     *
     * @see RatingChartResponse
     * @see ApiResponse
     */
    ApiResponse<List<RatingChartResponse>> chart();

    /**
     * Retrieves the application rating for a specified account.
     * If no account is provided, it retrieves the rating for the current user.
     *
     * @param id The ID of the account for which the rating is requested.
     * @return A response containing the application rating.
     *
     * @see ApiResponse
     */
    ApiResponse<Double> app(String id);
}