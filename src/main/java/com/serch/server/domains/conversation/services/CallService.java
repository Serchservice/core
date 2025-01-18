package com.serch.server.domains.conversation.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.conversation.requests.StartCallRequest;
import com.serch.server.domains.conversation.requests.UpdateCallRequest;
import com.serch.server.domains.conversation.responses.CallResponse;
import com.serch.server.domains.conversation.responses.ActiveCallResponse;

import java.util.List;

public interface CallService {
    /**
     * This starts a call and sends {@link ActiveCallResponse} containing the APP ID and other details
     * back to the caller
     *
     * @param request The {@link StartCallRequest} which contains the invited ID and call type
     *
     * @return {@link ApiResponse} of {@link ActiveCallResponse}
     */
    ApiResponse<ActiveCallResponse> start(StartCallRequest request);

    /**
     * Generate authentication code
     *
     * @return {@link ApiResponse} of call authentication token
     */
    ApiResponse<String> auth();

    /**
     * This starts a call and sends {@link ActiveCallResponse} containing the APP ID and other details
     * back to the caller
     *
     * @param channel The call channel
     *
     * @return {@link ApiResponse} of call authentication token
     */
    ApiResponse<String> auth(String channel);

    /**
     * Updates the call with the given data request and sends the update to the user {@link ActiveCallResponse}
     *
     * @param request The call data {@link UpdateCallRequest}
     */
    ApiResponse<ActiveCallResponse> update(UpdateCallRequest request);

    /**
     * Checks the call session and sends the update to the user {@link ActiveCallResponse} or throw an exception
     *
     * @param request The call data {@link UpdateCallRequest}
     */
    ApiResponse<ActiveCallResponse> checkSession(UpdateCallRequest request);

    /**
     * Fetches and sends the update to the user {@link ActiveCallResponse} or throw an exception
     *
     * @param channel The call channel
     */
    ApiResponse<ActiveCallResponse> fetch(String channel);

    /**
     * Get the list of calls the user's made or joined
     *
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @return List of {@link CallResponse}
     */
    ApiResponse<List<CallResponse>> logs(Integer page, Integer size);

    /**
     * This will close any call that is ringing but its wait time has passed 60 seconds
     */
    void closeRingingCalls();
}
