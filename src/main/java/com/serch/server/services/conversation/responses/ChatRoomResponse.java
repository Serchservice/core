package com.serch.server.services.conversation.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.chat.MessageStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ChatRoomResponse {
    private String room;
    private UUID roommate;
    private String name;
    private String avatar;
    private String category;
    private String image;
    private String label;
    private String message;
    private MessageStatus status;
    private Long count;
    private String bookmark;
    private String schedule;

    @JsonProperty("last_seen")
    private String lastSeen;

    @JsonProperty("is_bookmarked")
    private Boolean isBookmarked;

    @JsonProperty("is_scheduled")
    private Boolean isScheduled;

    @JsonProperty("sent_at")
    private LocalDateTime sentAt;

    @JsonIgnore
    private UUID user;

    private List<ChatGroupMessageResponse> groups = new ArrayList<>();
}