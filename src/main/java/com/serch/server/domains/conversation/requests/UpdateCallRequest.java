package com.serch.server.domains.conversation.requests;

import com.serch.server.enums.call.CallStatus;
import lombok.Data;

@Data
public class UpdateCallRequest {
    private String channel;
    private Integer duration;
    private CallStatus status;
    private String time;
}