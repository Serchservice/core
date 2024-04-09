package com.serch.server.services.conversation.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.services.conversation.requests.StartCallRequest;
import com.serch.server.services.conversation.responses.CallHistoryResponse;
import com.serch.server.services.conversation.responses.CallResponse;
import com.serch.server.services.conversation.responses.StartCallResponse;

import java.util.List;
import java.util.UUID;

public interface CallService {
    ApiResponse<StartCallResponse> start(StartCallRequest request);
    ApiResponse<StartCallResponse> accept(String channel);
    ApiResponse<StartCallResponse> cancel(String channel);
    ApiResponse<StartCallResponse> decline(String channel);
    ApiResponse<String> end(String channel);
    ApiResponse<List<CallHistoryResponse>> calls();
    ApiResponse<List<CallResponse>> view(UUID userId);
}
