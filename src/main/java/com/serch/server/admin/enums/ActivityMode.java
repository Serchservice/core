package com.serch.server.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActivityMode {
    SUPER_CREATE("Super Create"),
    PERMISSION_WRITE_ADD("Permission::write::add"),
    PERMISSION_UPDATE_ADD("Permission::update::add"),
    PERMISSION_READ_ADD("Permission::read::add"),
    PERMISSION_DELETE_ADD("Permission::delete::add"),
    READ("Read"),
    CREATE("Create"),
    DELETE("Delete"),
    UPDATE("Update"),
    PERMISSION_WRITE_REMOVE("Permission::write::remove"),
    PERMISSION_UPDATE_REMOVE("Permission::update::remove"),
    PERMISSION_READ_REMOVE("Permission::read::remove"),
    PERMISSION_DELETE_REMOVE("Permission::delete::remove"),
    LOGIN("Login"),
    LOGOUT("Logout"),
    PERMISSION_WRITE_REQUEST("Permission::write::request"),
    PERMISSION_UPDATE_REQUEST("Permission::update::request"),
    PERMISSION_READ_REQUEST("Permission::read::request"),
    PERMISSION_DELETE_REQUEST("Permission::delete::request"),
    TEAM_ADD("Team Add"),
    INVITE_ACCEPT("Invite Accept"),
    PERMISSION_WRITE_GRANT("Permission::write::grant"),
    PERMISSION_UPDATE_GRANT("Permission::update::grant"),
    PERMISSION_READ_GRANT("Permission::read::grant"),
    PERMISSION_DELETE_GRANT("Permission::delete::grant"),
    PASSWORD_CHANGE("Password Change"),
    PASSWORD_CHANGE_REQUEST("Password Change Request"),
    PERMISSION_WRITE_DECLINE("Permission::write::decline"),
    PERMISSION_UPDATE_DECLINE("Permission::update::decline"),
    PERMISSION_READ_DECLINE("Permission::read::decline"),
    PERMISSION_DELETE_DECLINE("Permission::delete::decline"),
    AVATAR_CHANGE("Avatar Change"),
    STATUS_CHANGE("Status Change"),
    ROLE_CHANGE("Role Change"),
    ACCOUNT_DELETE("Account Delete"),
    PROFILE_UPDATE("Profile Update"),
    MFA_CONSTRAINT_ENFORCED("Multi-Factor Authentication Enforced"),
    MFA_CONSTRAINT_REMOVED("Multi-Factor Authentication Removed"),
    MFA_LOGIN("Mfa Login"),;

    private final String value;
}
