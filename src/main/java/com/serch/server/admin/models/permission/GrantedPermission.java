package com.serch.server.admin.models.permission;

import com.serch.server.admin.enums.Permission;
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
@Table(schema = "admin", name = "granted_permissions")
public class GrantedPermission extends BaseModel {
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Permission must be an enum")
    private Permission permission;

    @ManyToOne
    @JoinColumn(
            name = "scope_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "perm_scope_fkey")
    )
    private GrantedPermissionScope scope;
}
