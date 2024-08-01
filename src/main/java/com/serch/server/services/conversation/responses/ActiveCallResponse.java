package com.serch.server.services.conversation.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.enums.call.CallType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ActiveCallResponse {
    private String app;
    private CallStatus status;
    private String channel;
    private String name;
    private CallType type;
    private UUID user;
    private String category;
    private String image;
    private String avatar;
    private String error;
    private Integer session;
    private String snt;

    @JsonProperty("is_caller")
    private Boolean isCaller;

    @JsonProperty("error_code")
    private String errorCode;
}
