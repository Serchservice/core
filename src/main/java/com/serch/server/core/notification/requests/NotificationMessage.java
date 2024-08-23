package com.serch.server.core.notification.requests;

import lombok.Data;

@Data
public class NotificationMessage<T> {
    private String token;
    private String snt;
    private SerchNotification<T> data;
}