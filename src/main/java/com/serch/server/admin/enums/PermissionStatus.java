package com.serch.server.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionStatus {
    PENDING,
    APPROVED,
    REJECTED,
    REVOKED
}
