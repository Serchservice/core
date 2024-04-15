package com.serch.server.enums.verified;

import lombok.Getter;

/**
 * The VerificationStage enum represents different stages of verification in the application.
 * Each enum constant corresponds to a specific stage of verification and provides a descriptive type.
 * <p></p>
 * The verification stages are:
 * <ul>
 *     <li>{@link VerificationStage#STARTED} - Indicates that verification has started</li>
 *     <li>{@link VerificationStage#SUBMITTED} - Indicates that verification has been initiated</li>
 *     <li>{@link VerificationStage#NOT_STARTED} - Indicates that verification has not been started</li>
 *     <li>{@link VerificationStage#COMPLETED} - Indicates that verification has been completed</li>
 * </ul>
 */
@Getter
public enum VerificationStage {
    STARTED("Started"),
    SUBMITTED("Verification Initiated"),
    NOT_STARTED("Verification Not Started"),
    COMPLETED("Verification Completed");

    private final String type;
    VerificationStage(String type) {
        this.type = type;
    }
}