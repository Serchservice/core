package com.serch.server.admin.models;

import com.serch.server.bases.BaseUser;
import com.serch.server.enums.auth.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "admin", name = "admins")
public class Admin extends BaseUser {
    @Column(columnDefinition = "TEXT")
    private String avatar;

    @Column(columnDefinition = "TEXT", nullable = false, unique = true)
    private String pass;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String position;

    @Column(columnDefinition = "TEXT")
    private String department = "N/A";

    @Column(columnDefinition = "TEXT", name = "fcm_token")
    private String fcmToken;

    @Column(name = "must_have_mfa")
    private Boolean mustHaveMFA = false;

    @ManyToOne
    @JoinColumn(
            name = "added_by_id",
            referencedColumnName = "serch_id",
            updatable = false,
            foreignKey = @ForeignKey(name = "admin_added_by_fkey")
    )
    private Admin admin;

    @ManyToOne
    @JoinColumn(
            name = "password_reset_requested_by_id",
            referencedColumnName = "serch_id",
            updatable = false,
            foreignKey = @ForeignKey(name = "admin_password_reset_requested_by_fkey")
    )
    private Admin passwordResetBy;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admin")
    private List<Admin> admins;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admin")
    private List<GrantedPermissionScope> scopes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "admin")
    private List<AdminActivity> activities;

    public boolean mfaEnforced() {
        return mustHaveMFA && !getUser().hasMFA();
    }

    public boolean isSuper() {
        return getUser().getRole() == Role.SUPER_ADMIN;
    }

    public boolean isAdmin() {
        return getUser().getRole() == Role.ADMIN;
    }
}