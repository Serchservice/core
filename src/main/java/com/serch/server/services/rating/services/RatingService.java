package com.serch.server.services.rating.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.rating.Rating;
import com.serch.server.services.rating.requests.RateAppRequest;
import com.serch.server.services.rating.requests.RateRequest;
import com.serch.server.services.rating.responses.RatingChartResponse;
import com.serch.server.services.rating.responses.RatingResponse;
import org.springframework.data.domain.Page;

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
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     *
     * @see RatingResponse
     * @see ApiResponse
     */
    ApiResponse<List<RatingResponse>> view(Integer page, Integer size);

    /**
     * Retrieves good ratings received by the current user.
     *
     * @param id The id of the user to find its rating
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     *
     * @return A response containing a list of good rating responses.
     *
     * @see RatingResponse
     * @see ApiResponse
     */
    ApiResponse<List<RatingResponse>> good(String id, Integer page, Integer size);

    /**
     * Retrieves bad ratings received by the current user.
     *
     * @param id The id of the user to find its rating
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     *
     * @return A response containing a list of bad rating responses.
     *
     * @see RatingResponse
     * @see ApiResponse
     */
    ApiResponse<List<RatingResponse>> bad(String id, Integer page, Integer size);

    /**
     * Builds a list of rating chart responses for a specific user or entity.
     * The chart provides visual data points for ratings, typically used
     * for analysis or reporting purposes.
     *
     * @param id The unique identifier of the user or entity for whom the chart
     *           is being generated. This ID should correspond to a valid user or entity.
     * @return A list of {@link RatingChartResponse} objects representing the rating chart data.
     */
    List<RatingChartResponse> buildChart(String id);

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
     * @param ratings The page of ratings
     *
     * @return {@link ApiResponse} of list of {@link RatingResponse}
     */
    ApiResponse<List<RatingResponse>> ratings(Page<Rating> ratings);
}