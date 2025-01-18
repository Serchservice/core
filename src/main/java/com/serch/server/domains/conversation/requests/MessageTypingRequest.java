package com.serch.server.domains.conversation.requests;

import lombok.Data;

@Data
public class MessageTypingRequest {
    private String room;
    private String state;
}