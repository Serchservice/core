package com.serch.server.admin.models;

import com.serch.server.admin.enums.PermissionScope;
import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "admin", name = "granted_permission_scopes")
public class GrantedPermissionScope extends BaseModel {
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Permission scope must be an enum")
    private PermissionScope scope;

    @Column(columnDefinition = "TEXT")
    private String account;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "scope")
    private Set<GrantedPermission> permissions;

    @ManyToOne
    @JoinColumn(
            name = "admin_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "scope_admin_fkey")
    )
    private Admin admin;
}
