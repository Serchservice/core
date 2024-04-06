package com.serch.server.enums.chat;

import lombok.Getter;

/// The MessageType enum has 6 cases representing different types of messages. The cases are:
///
/// `text`: represents a message containing only text.
///
/// `Image`: represents a message containing an image.
///
/// `Video`: represents a message containing a video.
///
/// `Audio`: represents a message containing an audio recording.
///
/// `Emoji`: represents a message containing an emoji.
///
/// `File`: represents a message containing a file.
///
/// `Voice`: represents a message containing a voice.
///
/// Each case has a sub property of substring which contains a description of the sub message.
/// The enum also has a constructor const MessageType(this.sub) which initializes the sub property for each case.
/// This enum represents different types of messages.
@Getter
public enum MessageType {
    /// Represents a text message.
    TEXT("Text"),

    /// Represents an image message.
    IMAGE("Image"),

    /// Represents a video message.
    VIDEO("Video"),

    /// Represents a voice message.
    VOICE("Voice"),

    /// Represents an audio message.
    AUDIO("Audio"),

    /// Represents an emoji message.
    EMOJI("Emoji"),

    /// Represents a file message.
    FILE("File");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }
}