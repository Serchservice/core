package com.serch.server.enums.chat;

import lombok.Getter;

@Getter
public enum MessageStatus {
    /// Represents a sending message.
    SENDING("Sending"),
    /// Represents a sent message.
    SENT("Sent"),
    /// Represents a delivered message.
    DELIVERED("Delivered"),
    /// Error in sending a message
    NOT_SENT("Not Sent"),
    /// Represents a read message.
    READ("Read");

    private final String type;
    MessageStatus(String type) {
        this.type = type;
    }
}