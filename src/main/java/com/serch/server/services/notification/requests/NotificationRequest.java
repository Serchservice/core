package com.serch.server.services.notification.requests;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Getter
@Setter
@Builder
public class NotificationRequest {
    private String title;
    private String image;
    private String message;
    private Map<String, String> data;
    private String topic;
    private String token;

    public boolean isTopic() {
        return getTopic() != null && !getTopic().isEmpty();
    }
}
