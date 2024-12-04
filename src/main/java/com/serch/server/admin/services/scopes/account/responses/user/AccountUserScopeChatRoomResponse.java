package com.serch.server.admin.services.scopes.account.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.chat.MessageState;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class AccountUserScopeChatRoomResponse {
    private String id;
    private MessageState state;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;
}