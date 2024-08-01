package com.serch.server.admin.models;

import com.serch.server.admin.enums.ActivityMode;
import com.serch.server.admin.enums.PermissionScope;
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
@Table(schema = "admin", name = "admin_activities")
public class AdminActivity extends BaseModel {
    @Column(columnDefinition = "TEXT")
    private String associated;

    @Column(columnDefinition = "TEXT")
    private String account;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Activity mode must be an enum")
    private ActivityMode mode;

    @Enumerated(value = EnumType.STRING)
    private PermissionScope scope;

    @ManyToOne
    @JoinColumn(
            name = "admin_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "activity_admin_fkey")
    )
    private Admin admin;
}
