package com.serch.server.enums.schedule;

import lombok.Getter;

/**
 * The ScheduleStatus enum represents different statuses of a schedule in the application.
 * Each enum constant corresponds to a specific schedule status and provides a descriptive type.
 * <p><p></p>
 * The schedule statuses are:
 * <ul>
 *     <li>{@link ScheduleStatus#PENDING} - Represents a pending schedule</li>
 *     <li>{@link ScheduleStatus#ACCEPTED} - Represents an accepted schedule</li>
 *     <li>{@link ScheduleStatus#DECLINED} - Represents a declined schedule</li>
 *     <li>{@link ScheduleStatus#CLOSED} - Represents a closed schedule</li>
 *     <li>{@link ScheduleStatus#ATTENDED} - Represents an attended schedule</li>
 *     <li>{@link ScheduleStatus#CANCELLED} - Represents a cancelled schedule</li>
 * </ul>
 */
@Getter
public enum ScheduleStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    DECLINED("Declined"),
    CLOSED("Closed"),
    ATTENDED("Attended"),
    CANCELLED("Cancelled");
    private final String type;

    ScheduleStatus(String type) {
      this.type = type;
    }
}