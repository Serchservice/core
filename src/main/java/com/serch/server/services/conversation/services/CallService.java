package com.serch.server.services.conversation.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.conversation.requests.StartCallRequest;
import com.serch.server.services.conversation.responses.CallResponse;
import com.serch.server.services.conversation.responses.StartCallResponse;

import java.util.List;

public interface CallService {
    ApiResponse<StartCallResponse> start(StartCallRequest request);
    ApiResponse<StartCallResponse> answer(String channel);
    ApiResponse<StartCallResponse> cancel(String channel);
    ApiResponse<StartCallResponse> decline(String channel);
    ApiResponse<StartCallResponse> end(String channel);
    ApiResponse<List<CallResponse>> logs();
    ApiResponse<String> checkSession(Integer duration, String channel);
}
