package com.serch.server.core.notification.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Notification {
    private String title;
    private String body;
    private String image;
}
