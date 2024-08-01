package com.serch.server.admin.services.notification;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AdminNotificationStatResponse {
    private Integer unread;
    private List<AdminNotificationResponse> notifications = new ArrayList<>();
}
