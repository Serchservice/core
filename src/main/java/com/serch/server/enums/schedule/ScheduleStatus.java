package com.serch.server.enums.schedule;

import lombok.Getter;

@Getter
public enum ScheduleStatus {
    /// Represents a pending schedule.
    PENDING("Pending"),
    /// Represents a noAction schedule.
    NO_ACTION("No Action"),
    /// Represents an accepted schedule.
    ACCEPTED("Accepted"),
    /// Represents a not accepted schedule.
    NOT_ACCEPTED("Not Accepted"),
    /// Represents an ended schedule
    ENDED("Ended"),
    /// Represents a closed schedule
    CLOSED("Closed"),
    /// Represents a cancelled schedule.
    CANCELLED("Cancelled");
    private final String type;

    ScheduleStatus(String type) {
      this.type = type;
    }
}