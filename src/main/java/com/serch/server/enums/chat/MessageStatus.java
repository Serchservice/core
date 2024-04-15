package com.serch.server.enums.chat;

import lombok.Getter;

/**
 * The MessageStatus enum represents different statuses of a message in the application.
 * Each enum constant corresponds to a specific message status and provides a descriptive type.
 * <p></p>
 * The message statuses are:
 * <ul>
 *     <li>{@link MessageStatus#SENDING} - Represents a sending message</li>
 *     <li>{@link MessageStatus#SENT} - Represents a sent message</li>
 *     <li>{@link MessageStatus#DELIVERED} - Represents a delivered message</li>
 *     <li>{@link MessageStatus#NOT_SENT} - Represents an error in sending a message</li>
 *     <li>{@link MessageStatus#READ} - Represents a read message</li>
 * </ul>
 * This enum is annotated with Lombok's {@link Getter} to generate getter methods automatically.
 */
@Getter
public enum MessageStatus {
    SENDING("Sending"),
    SENT("Sent"),
    DELIVERED("Delivered"),
    NOT_SENT("Not Sent"),
    READ("Read");

    private final String type;
    MessageStatus(String type) {
        this.type = type;
    }
}