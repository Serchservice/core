package com.serch.server.enums.call;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The CallType enum represents different types of calls in the application.
 * Each enum constant corresponds to a specific call type and provides a descriptive type.
 * <p></p>
 * The call types are:
 * <ul>
 *     <li>{@link CallType#VOICE} - Represents a voice call</li>
 *     <li>{@link CallType#T2F} - Represents a tip-to-fix (Tip2Fix) call</li>
 *     <li>{@link CallType#NONE} - Represents a none call</li>
 * </ul>
 * This enum is annotated with Lombok's {@link Getter} and {@link RequiredArgsConstructor} to generate
 * getter methods and a constructor with required arguments automatically.
 */
@Getter
@RequiredArgsConstructor
public enum CallType {
    VOICE("Voice"),
    T2F("T2F"),
    NONE("None");

    private final String type;
}
