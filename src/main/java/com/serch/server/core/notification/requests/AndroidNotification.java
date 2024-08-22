package com.serch.server.core.notification.requests;

import lombok.Data;

@Data
public class AndroidNotification {
    private String icon = "resource://raw/res_favicon_dark";
    private String color = "#050404";
    private String sound = "resource://raw/res_notify";
}
