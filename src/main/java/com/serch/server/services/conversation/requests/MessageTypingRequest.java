package com.serch.server.services.conversation.requests;

import lombok.Data;

@Data
public class MessageTypingRequest {
    private String room;
    private String state;
}