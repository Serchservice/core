package com.serch.server.core.notification.requests;

import lombok.Data;

@Data
public class AndroidNotification {
    private String icon = "res_favicon_dark";
    private String color = "#050404";
    private String sound = "res_notify";
}
