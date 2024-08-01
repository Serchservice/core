package com.serch.server.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminNotificationType {
    PERMISSION_REQUEST("Permission Request"),
    DEFAULT("Default");

    private final String value;
}