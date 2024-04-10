package com.serch.server.services.notification.core;

import com.serch.server.services.notification.requests.NotificationRequest;

import java.util.List;

public interface NotificationService {
    void send(NotificationRequest request);
    void send(List<String> tokens, NotificationRequest request);
    void subscribe(String topic, List<String> tokens);
}
