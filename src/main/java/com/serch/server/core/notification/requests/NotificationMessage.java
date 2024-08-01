package com.serch.server.core.notification.requests;

import lombok.Data;

@Data
public class NotificationMessage<T> {
    private String token;
    private Notification notification;
    private T data;
    private NotificationAndroid android = new NotificationAndroid();
}