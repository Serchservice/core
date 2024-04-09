package com.serch.server.services.conversation.responses;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CallTimeInfo {
    private String start;
    private String end;
}
