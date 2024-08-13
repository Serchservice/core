package com.serch.server.services.conversation.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.services.conversation.requests.StartCallRequest;
import com.serch.server.services.conversation.responses.CallResponse;
import com.serch.server.services.conversation.responses.ActiveCallResponse;

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
     * Updates the call with the given status {@link CallStatus} and sends
     * updated {@link ActiveCallResponse} containing the APP ID and other details to both users in the channel
     *
     * @param channel The call channel
     * @param status The status to be updated with
     */
    void update(String channel, CallStatus status);

    /**
     * Answer the call the user received and sends {@link ActiveCallResponse} containing the APP ID and other details
     * to the call receiver and also change the call details of the caller
     *
     * @param channel The call channel to join
     */
    void answer(String channel);

    /**
     * Decline the call the user received and sends
     * updated {@link ActiveCallResponse} containing the APP ID and other details to both users in the call channel
     *
     * @param channel The call channel to join
     */
    void decline(String channel);

    /**
     * End an active call and send updated {@link ActiveCallResponse} containing the APP ID and other details
     * to both users in the call channel
     *
     * @param channel The call channel to join
     */
    void end(String channel);

    /**
     * Check the call session periodically to notify any changes or end the call for both users in the call channel
     *
     * @param duration The current duration
     * @param channel The call channel
     */
    void checkSession(Integer duration, String channel);

    /**
     * Get the list of calls the user's made or joined
     *
     * @return List of {@link CallResponse}
     */
    ApiResponse<List<CallResponse>> logs();

    /**
     * This will close any call that is ringing but its wait time has passed 60 seconds
     */
    void closeRingingCalls();
}
