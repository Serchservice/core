package com.serch.server.enums.call;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The CallStatus enum represents different call statuses in the application.
 * Each enum constant corresponds to a specific call status and provides a descriptive type.
 * <p></p>
 * The call statuses are:
 * <ul>
 *     <li>{@link CallStatus#CALLING} - Represents calling status</li>
 *     <li>{@link CallStatus#RINGING} - Represents ringing status</li>
 *     <li>{@link CallStatus#ON_CALL} - Represents on-call status</li>
 *     <li>{@link CallStatus#CLOSED} - Represents closed call status</li>
 *     <li>{@link CallStatus#DECLINED} - Represents declined call status</li>
 *     <li>{@link CallStatus#MISSED} - Indicates that the user missed the call</li>
 * </ul>
 * This enum is annotated with Lombok's {@link Getter} and {@link RequiredArgsConstructor} to generate
 * getter methods and a constructor with required arguments automatically.
 */
@Getter
@RequiredArgsConstructor
public enum CallStatus {
    CALLING("Calling"),
    RINGING("Ringing"),
    ON_CALL("On Call"),
    CLOSED("Closed"),
    DECLINED("Declined"),
    MISSED("Missed");

    private final String type;
}
