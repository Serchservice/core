package com.serch.server.enums.verified;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VerificationStatus {
    /// This is the status that shows that the verification status is ongoing.
    REQUESTED("Requested"),
    /// This is the status that shows that the verification status is completed and checked to be `TRUE`.
    VERIFIED("Verified"),
    /// This is the status that shows that the verification status is not verified.
    NOT_VERIFIED("Not Verified"),
    /// This is the status that shows that there is an error with the verification.
    ERROR("Error");

    private final String type;
}