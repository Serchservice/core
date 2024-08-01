package com.serch.server.core.notification.requests;

import lombok.Data;

@Data
public class FCM {
    private NotificationMessage<?> message;
}