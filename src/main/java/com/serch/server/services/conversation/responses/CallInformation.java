package com.serch.server.services.conversation.responses;

import com.serch.server.enums.call.CallStatus;
import com.serch.server.enums.call.CallType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CallInformation {
    private String label;
    private String duration;
    private Boolean outgoing;
    private CallType type;
    private CallStatus status;
}