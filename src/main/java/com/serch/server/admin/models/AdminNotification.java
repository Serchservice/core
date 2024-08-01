package com.serch.server.admin.models;

import com.serch.server.admin.enums.AdminNotificationStatus;
import com.serch.server.admin.enums.AdminNotificationType;
import com.serch.server.bases.BaseModel;
import com.serch.server.models.auth.User;
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
@Table(schema = "admin", name = "admin_notifications")
public class AdminNotification extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT", updatable = false)
    private String message;

    @Column(columnDefinition = "TEXT", updatable = false)
    private String event;

    @Column(nullable = false, updatable = false)
    @Enumerated(value = EnumType.STRING)
    private AdminNotificationType type = AdminNotificationType.DEFAULT;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AdminNotificationStatus status = AdminNotificationStatus.UNREAD;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "notification_user_fkey")
    )
    private User user;
}
