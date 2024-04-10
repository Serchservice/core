package com.serch.server.services.conversation.responses;

import com.serch.server.enums.call.CallStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StartCallResponse {
    private String app;
    private CallStatus status;
    private String channel;
}
