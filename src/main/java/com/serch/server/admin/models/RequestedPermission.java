package com.serch.server.admin.models;

import com.serch.server.admin.enums.Permission;
import com.serch.server.admin.enums.PermissionScope;
import com.serch.server.admin.enums.PermissionStatus;
import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "admin", name = "admin_pending_permission_requests")
public class RequestedPermission extends BaseModel {
    @Enumerated(value = EnumType.STRING)
    private PermissionScope scope;

    @Column(columnDefinition = "TEXT")
    private String account;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Permission must be an enum")
    private Permission permission;

    @Enumerated(value = EnumType.STRING)
    private PermissionStatus status = PermissionStatus.PENDING;

    @ManyToOne
    @JoinColumn(
            name = "admin_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "scope_admin_fkey")
    )
    private Admin admin;

    @ManyToOne
    @JoinColumn(
            name = "updated_by_admin_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "scope_updated_by_admin_fkey")
    )
    private Admin updatedBy;
}
