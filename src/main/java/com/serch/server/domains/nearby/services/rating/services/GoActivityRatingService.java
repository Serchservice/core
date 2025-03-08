package com.serch.server.domains.nearby.services.rating.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.rating.requests.GoActivityRatingRequest;
import com.serch.server.domains.nearby.services.rating.responses.GoActivityRatingResponse;

import java.util.List;

public interface GoActivityRatingService {
    /**
     * Rates on an activity.
     *
     * @param request The {@link GoActivityRatingRequest} details of the activity to rating.
     * @return An {@link ApiResponse} containing a success message or an error message with nullable {@link GoActivityRatingResponse}.
     */
    ApiResponse<GoActivityRatingResponse> rate(GoActivityRatingRequest request);

    /**
     * Retrieves a list of activity ratings based on the activity being viewed.
     *
     * @param page The page number for pagination.
     * @param size The number of ratings per page.
     * @param activity The ID of the activity associated with the rating.
     * @return An {@link ApiResponse} containing a list of {@link GoActivityRatingResponse} objects, or an error message.
     */
    ApiResponse<List<GoActivityRatingResponse>> getRatings(Integer page, Integer size, String activity);
}