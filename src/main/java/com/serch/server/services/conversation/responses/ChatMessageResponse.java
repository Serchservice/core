package com.serch.server.services.conversation.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.chat.MessageStatus;
import com.serch.server.enums.chat.MessageType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {
    private String id;
    private String label;
    private String room;
    private String message;
    private MessageStatus status;
    private MessageType type;
    private String duration;
    private String name;
    private ChatReplyResponse reply;

    @JsonProperty("has_only_emojis")
    private Boolean hasOnlyEmojis;

    @JsonProperty("has_only_one_emoji")
    private Boolean hasOnlyOneEmoji;

    @JsonProperty("file_size")
    private String fileSize;

    @JsonProperty("is_sent_by_current_user")
    private Boolean isSentByCurrentUser;

    @JsonProperty("sent_at")
    private LocalDateTime sentAt;
}