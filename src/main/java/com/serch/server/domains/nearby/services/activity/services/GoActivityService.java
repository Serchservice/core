package com.serch.server.domains.nearby.services.activity.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.activity.requests.GoCreateActivityRequest;
import com.serch.server.domains.nearby.services.activity.responses.GoActivityResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * This interface defines the contract for managing activities.
 * It provides methods for creating, attending, retrieving, and deleting activities.
 */
public interface GoActivityService {
    /**
     * Creates a new activity.
     *
     * @param request The {@link GoCreateActivityRequest} object containing the activity details.
     * @return An {@link ApiResponse} containing the created {@link GoActivityResponse} object, or an error message.
     */
    ApiResponse<GoActivityResponse> create(GoCreateActivityRequest request);

    /**
     * Allows a user to attend an activity.
     *
     * @param id The ID of the activity to attend.
     * @return An {@link ApiResponse} containing the updated {@link GoActivityResponse} object, or an error message.
     */
    ApiResponse<GoActivityResponse> attend(String id);

    /**
     * Retrieves a list of activities based on specified criteria.
     *
     * @param lng The longitude of the user's address.
     * @param lat The latitude of the user's address.
     * @param page The page number for pagination.
     * @param size The number of activities per page.
     * @param interest The ID of the interest associated with the activities.
     * @param timestamp The timestamp for filtering activities (e.g., upcoming activities).
     * @param scoped A flag indicating whether to filter activities created by the current user.
     * @return An {@link ApiResponse} containing a list of {@link GoActivityResponse} objects, or an error message.
     */
    ApiResponse<List<GoActivityResponse>> getAll(Integer page, Integer size, Long interest, LocalDate timestamp, Boolean scoped, Double lat, Double lng);

    /**
     * Retrieves a list of ongoing activities for the logged-in user based on specified criteria.
     *
     * @param lng The longitude of the user's address.
     * @param lat The latitude of the user's address.
     * @param page The page number for pagination.
     * @param size The number of activities per page.
     * @param scoped A flag indicating whether to filter activities created by the current user.
     *
     * @return An {@link ApiResponse} containing a list of {@link GoActivityResponse} objects, or an error message.
     */
    ApiResponse<List<GoActivityResponse>> getAllOngoing(Integer page, Integer size, Boolean scoped, Double lat, Double lng);

    /**
     * Retrieves a list of upcoming activities for the logged-in user based on specified criteria.
     *
     * @param lng The longitude of the user's address.
     * @param lat The latitude of the user's address.
     * @param page The page number for pagination.
     * @param size The number of activities per page.
     * @param scoped A flag indicating whether to filter activities created by the current user.
     *
     * @return An {@link ApiResponse} containing a list of {@link GoActivityResponse} objects, or an error message.
     */
    ApiResponse<List<GoActivityResponse>> getAllUpcoming(Integer page, Integer size, Boolean scoped, Double lat, Double lng);

    /**
     * Retrieves a list of attended activities for the logged-in user based on specified criteria.
     *
     * @param lng The longitude of the user's address.
     * @param lat The latitude of the user's address.
     * @param page The page number for pagination.
     * @param size The number of activities per page.
     *
     * @return An {@link ApiResponse} containing a list of {@link GoActivityResponse} objects, or an error message.
     */
    ApiResponse<List<GoActivityResponse>> getAllAttended(Integer page, Integer size, Double lat, Double lng);

    /**
     * Retrieves a list of attending activities for the logged-in user based on specified criteria.
     *
     * @param lng The longitude of the user's address.
     * @param lat The latitude of the user's address.
     * @param page The page number for pagination.
     * @param size The number of activities per page.
     *
     * @return An {@link ApiResponse} containing a list of {@link GoActivityResponse} objects, or an error message.
     */
    ApiResponse<List<GoActivityResponse>> getAllAttending(Integer page, Integer size, Double lat, Double lng);

    /**
     * Searches for a list of activities based on specified criteria with search value.
     *
     * @param scoped A flag indicating whether to filter activities with the user's interests.
     * @param lng The longitude of the user's address.
     * @param lat The latitude of the user's address.
     * @param page The page number for pagination.
     * @param size The number of activities per page.
     * @param q The search query.
     * @return An {@link ApiResponse} containing a list of {@link GoActivityResponse} objects, or an error message.
     */
    ApiResponse<List<GoActivityResponse>> search(Integer page, Integer size, String q, Double lat, Double lng, Boolean scoped);

    /**
     * Retrieves a list of similar activities based on the activity being viewed where the activity's interest matches any other activity's interest.
     *
     * @param lng The longitude of the user's address.
     * @param lat The latitude of the user's address.
     * @param page The page number for pagination.
     * @param size The number of activities per page.
     * @param event The ID of the activity associated with the activities.
     * @return An {@link ApiResponse} containing a list of {@link GoActivityResponse} objects, or an error message.
     */
    ApiResponse<List<GoActivityResponse>> getSimilar(Integer page, Integer size, String event, Double lat, Double lng);

    /**
     * Retrieves a list of related activities based on the activity being viewed where the user's id matches any other related activity's interest create by the same user.
     *
     * @param lng The longitude of the user's address.
     * @param lat The latitude of the user's address.
     * @param page The page number for pagination.
     * @param size The number of activities per page.
     * @param event The ID of the activity associated with the activities.
     * @return An {@link ApiResponse} containing a list of {@link GoActivityResponse} objects, or an error message.
     */
    ApiResponse<List<GoActivityResponse>> getRelated(Integer page, Integer size, String event, Double lat, Double lng);

    /**
     * Retrieves a specific activity by its ID.
     *
     * @param lng The longitude of the user's address.
     * @param lat The latitude of the user's address.
     * @param id The ID of the activity to retrieve.
     * @return An {@link ApiResponse} containing the {@link GoActivityResponse} object, or an error message.
     */
    ApiResponse<GoActivityResponse> get(String id, Double lat, Double lng);

    /**
     * Ends a specific activity by its ID.
     *
     * @param id The ID of the activity to retrieve.
     * @return An {@link ApiResponse} containing the {@link GoActivityResponse} object, or an error message.
     */
    ApiResponse<GoActivityResponse> end(String id);

    /**
     * Starts a specific activity by its ID.
     *
     * @param id The ID of the activity to retrieve.
     * @return An {@link ApiResponse} containing the {@link GoActivityResponse} object, or an error message.
     */
    ApiResponse<GoActivityResponse> start(String id);

    /**
     * Deletes an activity.
     *
     * @param id The ID of the activity to delete.
     * @return An {@link ApiResponse} containing a success message or an error message.
     */
    ApiResponse<Void> delete(String id);
}