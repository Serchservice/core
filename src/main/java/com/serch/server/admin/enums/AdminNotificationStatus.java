package com.serch.server.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminNotificationStatus {
    READ("Read"),
    UNREAD("Unread");

    private final String value;
}