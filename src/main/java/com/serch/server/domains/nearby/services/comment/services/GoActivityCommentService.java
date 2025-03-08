package com.serch.server.domains.nearby.services.comment.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.comment.requests.GoActivityCommentRequest;
import com.serch.server.domains.nearby.services.comment.responses.GoActivityCommentResponse;

import java.util.List;

public interface GoActivityCommentService {
    /**
     * Comments on an activity.
     *
     * @param request The {@link GoActivityCommentRequest} details of the activity to comment.
     * @return An {@link ApiResponse} containing a success message or an error message with nullable {@link GoActivityCommentResponse}.
     */
    ApiResponse<GoActivityCommentResponse> comment(GoActivityCommentRequest request);

    /**
     * Retrieves a list of activity comments based on the activity being viewed.
     *
     * @param page The page number for pagination.
     * @param size The number of comments per page.
     * @param activity The ID of the activity associated with the comments.
     * @return An {@link ApiResponse} containing a list of {@link GoActivityCommentResponse} objects, or an error message.
     */
    ApiResponse<List<GoActivityCommentResponse>> getComments(Integer page, Integer size, String activity);
}