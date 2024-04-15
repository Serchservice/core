package com.serch.server.enums.chat;

import lombok.Getter;

/**
 * The MessageType enum represents different types of messages in the application.
 * Each enum constant corresponds to a specific message type and provides a descriptive type.
 * <p></p>
 * The message types are:
 * <ul>
 *     <li>{@link MessageType#TEXT} - Represents a text message</li>
 *     <li>{@link MessageType#IMAGE} - Represents an image message</li>
 *     <li>{@link MessageType#VIDEO} - Represents a video message</li>
 *     <li>{@link MessageType#VOICE} - Represents a voice message</li>
 *     <li>{@link MessageType#AUDIO} - Represents an audio message</li>
 *     <li>{@link MessageType#EMOJI} - Represents an emoji message</li>
 *     <li>{@link MessageType#FILE} - Represents a file message</li>
 * </ul>
 * Each enum constant also has a sub property named "substring" which contains a description of the sub message.
 * This enum is annotated with Lombok's {@link Getter} to generate getter methods automatically.
 */
@Getter
public enum MessageType {
    TEXT("Text"),
    IMAGE("Image"),
    VIDEO("Video"),
    VOICE("Voice"),
    AUDIO("Audio"),
    EMOJI("Emoji"),
    FILE("File");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }
}