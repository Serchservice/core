package com.serch.server.services.conversation.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.services.conversation.requests.StartCallRequest;
import com.serch.server.services.conversation.responses.CallResponse;
import com.serch.server.services.conversation.responses.StartCallResponse;

import java.util.List;
import java.util.Map;

public interface CallService {
    /**
     * This starts a call
     *
     * @param request The {@link StartCallRequest} which contains the invited ID and call type
     *
     * @return {@link StartCallResponse} containing the APP ID and other details
     *
     * @see ApiResponse
     */
    ApiResponse<StartCallResponse> start(StartCallRequest request);

    /**
     * Generates a token for RTC - Agora
     *
     * @param channel The call channel
     * @param uid The uid of the user in the call
     *
     * @return {@link Map} of the generated token with "rtcToken"
     */
    Map<String, String> authenticate(String channel, Integer uid);

    /**
     * This verifies a call from the invited side
     *
     * @param channel The call channel
     *
     * @return {@link StartCallResponse} containing the APP ID and other details
     *
     * @see ApiResponse
     */
    ApiResponse<StartCallResponse> verify(String channel);

    /**
     * Updates the call with the given status {@link CallStatus}
     *
     * @param channel The call channel
     * @param status The status to be updated with
     *
     * @return {@link StartCallResponse} containing the APP ID and other details
     *
     * @see ApiResponse
     */
    ApiResponse<StartCallResponse> update(String channel, CallStatus status);
    ApiResponse<StartCallResponse> answer(String channel);
    ApiResponse<StartCallResponse> decline(String channel);
    ApiResponse<StartCallResponse> end(String channel);
    ApiResponse<List<CallResponse>> logs();
    ApiResponse<String> checkSession(Integer duration, String channel);

    /**
     * This will close any call that is ringing but its wait time has passed 60 seconds
     */
    void closeRingingCalls();
}
