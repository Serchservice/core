package com.serch.server.enums.chat;

import lombok.Getter;

/**
 * The MessageState enum represents different states of a message in the application.
 * Each enum constant corresponds to a specific message state and provides a descriptive type.
 * <p></p>
 * The message states are:
 * <ul>
 *     <li>{@link MessageState#ACTIVE} - Represents an active message</li>
 *     <li>{@link MessageState#DELETED} - Represents a deleted message</li>
 *     <li>{@link MessageState#REMOVED} - Represents a removed message</li>
 * </ul>
 * This enum is annotated with Lombok's {@link Getter} to generate getter methods automatically.
 */
@Getter
public enum MessageState {
    ACTIVE("Active"),
    DELETED("Deleted"),
    REMOVED("Removed");

    private final String type;
    MessageState(String type) {
        this.type = type;
    }
}