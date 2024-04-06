package com.serch.server.enums.chat;

import lombok.Getter;

@Getter
public enum MessageState {
    /// Represents a active message.
    ACTIVE("Active"),
    /// Represents an deleted message.
    DELETED("Deleted"),
    /// Represents a removed message.
    REMOVED("Removed");

    private final String type;
    MessageState(String type) {
        this.type = type;
    }
}