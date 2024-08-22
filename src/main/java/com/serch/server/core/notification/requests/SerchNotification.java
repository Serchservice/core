package com.serch.server.core.notification.requests;

import lombok.Data;

@Data
public class SerchNotification<T> {
    private String title;
    private String body;
    private String image;
    private T data;
}
