package com.serch.server.services.conversation.requests;

import com.serch.server.enums.chat.MessageState;
import com.serch.server.enums.chat.MessageStatus;
import lombok.Data;

@Data
public class UpdateMessageRequest {
    private String id;
    private String room;
    private MessageStatus status;
    private MessageState state;
}
