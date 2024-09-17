package com.serch.server.services.conversation.responses;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChatGroupMessageResponse {
    private String label;
    private ZonedDateTime time;
    private List<ChatMessageResponse> messages = new ArrayList<>();
}