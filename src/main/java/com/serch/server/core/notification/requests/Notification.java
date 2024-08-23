package com.serch.server.core.notification.requests;

import lombok.Data;

@Data
public class Notification {
    private String title;
    private String body;
    private String image;
}
