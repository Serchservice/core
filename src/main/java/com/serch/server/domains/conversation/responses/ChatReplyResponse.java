package com.serch.server.domains.conversation.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.chat.MessageStatus;
import com.serch.server.enums.chat.MessageType;
import lombok.Data;

@Data
public class ChatReplyResponse {
    private String id;
    private String label;
    private String message;
    private String duration;
    private MessageType type;
    private String sender;
    private MessageStatus status;

    @JsonProperty("file_size")
    private String fileSize;

    @JsonProperty("is_sent_by_current_user")
    private Boolean isSentByCurrentUser;
}