package com.serch.server.admin.services.notification.services;

import com.serch.server.admin.enums.AdminNotificationType;
import com.serch.server.models.auth.User;

public interface AdminNotificationService {
    void create(String message, String event, AdminNotificationType type, User user);
    void notifications();
    void markAsRead(Long id);
    void clear(Long id);
    void clearAll();
}