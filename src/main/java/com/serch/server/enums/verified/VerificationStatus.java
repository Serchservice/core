package com.serch.server.enums.verified;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The VerificationStatus enum represents different verification statuses in the application.
 * Each enum constant corresponds to a specific verification status and provides a descriptive type.
 * <p></p>
 * The verification statuses are:
 * <ul>
 *     <li>{@link VerificationStatus#REQUESTED} - Indicates that verification has been requested</li>
 *     <li>{@link VerificationStatus#VERIFIED} - Indicates that verification has been successfully completed</li>
 *     <li>{@link VerificationStatus#NOT_VERIFIED} - Indicates that verification has not been completed</li>
 *     <li>{@link VerificationStatus#ERROR} - Indicates an error occurred during verification</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public enum VerificationStatus {
    REQUESTED("Requested"),
    VERIFIED("Verified"),
    NOT_VERIFIED("Not Verified"),
    NONE("None"),
    ERROR("Error");

    private final String type;
}