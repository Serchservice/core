package com.serch.server.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionType {
    CLUSTER("Cluster"),
    SPECIFIC("Specific");

    private final String type;
}