package com.serch.server.domains.conversation.requests;

import com.serch.server.enums.call.CallType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class StartCallRequest {
    private UUID user;
    private CallType type;
}
