package com.serch.server.core.notification.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotificationAndroid {
    @JsonProperty("direct_boot_ok")
    private Boolean directBootOk = true;
}