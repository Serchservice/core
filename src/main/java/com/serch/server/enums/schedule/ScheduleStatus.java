package com.serch.server.enums.schedule;

import lombok.Getter;

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