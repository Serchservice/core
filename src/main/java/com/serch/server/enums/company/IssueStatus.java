package com.serch.server.enums.company;

import lombok.Getter;

@Getter
public enum IssueStatus {
    /// To tell if the issue is open
    OPENED("Opened"),
    /// To tell if the issue is pending
    PENDING("Pending"),
    /// To tell if the issue is resolved
    RESOLVED("Resolved"),
    /// To tell if the issue is closed
    CLOSED("Closed"),
    /// To tell if the issue is awaiting response from the opener/responder
    WAITING("Waiting for response");

    private final String type;
    IssueStatus(String type) {
        this.type = type;
    }
}
