package com.serch.server.utils;

import com.serch.server.admin.enums.ActivityMode;
import com.serch.server.admin.enums.Permission;

public class ActivityUtils {
    public static ActivityMode getRequest(Permission permission) {
        return switch (permission) {
            case READ -> ActivityMode.PERMISSION_READ_REQUEST;
            case WRITE -> ActivityMode.PERMISSION_WRITE_REQUEST;
            case DELETE -> ActivityMode.PERMISSION_DELETE_REQUEST;
            default -> ActivityMode.PERMISSION_UPDATE_REQUEST;
        };
    }

    public static ActivityMode getGrant(Permission permission) {
        return switch (permission) {
            case READ -> ActivityMode.PERMISSION_READ_GRANT;
            case WRITE -> ActivityMode.PERMISSION_WRITE_GRANT;
            case DELETE -> ActivityMode.PERMISSION_DELETE_GRANT;
            default -> ActivityMode.PERMISSION_UPDATE_GRANT;
        };
    }

    public static ActivityMode getDecline(Permission permission) {
        return switch (permission) {
            case READ -> ActivityMode.PERMISSION_READ_DECLINE;
            case WRITE -> ActivityMode.PERMISSION_WRITE_DECLINE;
            case DELETE -> ActivityMode.PERMISSION_DELETE_DECLINE;
            default -> ActivityMode.PERMISSION_UPDATE_DECLINE;
        };
    }

    public static ActivityMode getAdd(Permission permission) {
        return switch (permission) {
            case READ -> ActivityMode.PERMISSION_READ_ADD;
            case WRITE -> ActivityMode.PERMISSION_WRITE_ADD;
            case DELETE -> ActivityMode.PERMISSION_DELETE_ADD;
            default -> ActivityMode.PERMISSION_UPDATE_ADD;
        };
    }
}
