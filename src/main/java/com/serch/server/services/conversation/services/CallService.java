package com.serch.server.services.conversation.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.conversation.requests.CheckTip2FixSessionRequest;
import com.serch.server.services.conversation.requests.StartCallRequest;
import com.serch.server.services.conversation.responses.CallResponse;
import com.serch.server.services.conversation.responses.StartCallResponse;

import java.util.List;
import java.util.UUID;

public interface CallService {
    ApiResponse<StartCallResponse> start(StartCallRequest request);
    ApiResponse<StartCallResponse> answer(String channel);
    ApiResponse<StartCallResponse> cancel(String channel);
    ApiResponse<StartCallResponse> decline(String channel);
    ApiResponse<StartCallResponse> end(String channel);
    ApiResponse<List<CallResponse>> logs(UUID userId);
    ApiResponse<String> checkSession(CheckTip2FixSessionRequest request);
}
