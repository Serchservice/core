package com.serch.server.admin.services.notification;

import com.serch.server.admin.enums.AdminNotificationStatus;
import com.serch.server.admin.enums.AdminNotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminNotificationResponse {
    private Long id;
    private String message;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private String event;
    private String image;
    private AdminNotificationType type;
    private AdminNotificationStatus status;
}
