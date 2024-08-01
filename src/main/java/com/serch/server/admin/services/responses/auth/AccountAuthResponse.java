package com.serch.server.admin.services.responses.auth;

import lombok.Data;

import java.util.List;

@Data
public class AccountAuthResponse {
    private Boolean hasMFA;
    private Boolean mustHaveMFA;
    private String method;
    private String level;
    private List<AccountAuthDeviceResponse> devices;
}
