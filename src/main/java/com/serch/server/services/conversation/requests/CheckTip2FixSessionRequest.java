package com.serch.server.services.conversation.requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CheckTip2FixSessionRequest {
    private Integer duration;
    private String channel;
}
