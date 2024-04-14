package com.serch.server.enums.company;

import lombok.Getter;

/**
 * The IssueStatus enum represents different statuses of an issue in the application.
 * Each enum constant corresponds to a specific issue status and provides a descriptive type.
 * <p></p>
 * The issue statuses are:
 * <ul>
 *     <li>{@link IssueStatus#OPENED} - To indicate that the issue is open</li>
 *     <li>{@link IssueStatus#PENDING} - To indicate that the issue is pending</li>
 *     <li>{@link IssueStatus#RESOLVED} - To indicate that the issue is resolved</li>
 *     <li>{@link IssueStatus#CLOSED} - To indicate that the issue is closed</li>
 *     <li>{@link IssueStatus#WAITING} - To indicate that the issue is awaiting response from the opener/responder</li>
 * </ul>
 * This enum is annotated with Lombok's {@link Getter} to generate getter methods automatically.
 */
@Getter
public enum IssueStatus {
    OPENED("Opened"),
    PENDING("Pending"),
    RESOLVED("Resolved"),
    CLOSED("Closed"),
    WAITING("Waiting for response");

    private final String type;
    IssueStatus(String type) {
        this.type = type;
    }
}
