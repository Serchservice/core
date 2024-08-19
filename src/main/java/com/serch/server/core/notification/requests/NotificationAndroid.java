package com.serch.server.core.notification.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotificationAndroid {
    @JsonProperty("direct_boot_ok")
    private Boolean directBootOk = true;

    private String icon = "resource://raw/res_favicon_dark";
    private String color = "#050404";
    private String sound = "resource://raw/res_notify";
}