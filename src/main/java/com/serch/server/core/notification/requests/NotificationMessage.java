package com.serch.server.core.notification.requests;

import com.serch.server.enums.NotificationType;
import lombok.Data;

@Data
public class NotificationMessage<T> {
    private String token;
    private NotificationType snt;
    private SerchNotification<T> data;
}