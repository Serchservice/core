package com.serch.server.core.notification.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotificationAndroid {
    @JsonProperty("direct_boot_ok")
    private Boolean directBootOk = true;

    @JsonProperty("notification_priority")
    private String notificationPriority = "PRIORITY_HIGH";

    private AndroidNotification notification = new AndroidNotification();
}