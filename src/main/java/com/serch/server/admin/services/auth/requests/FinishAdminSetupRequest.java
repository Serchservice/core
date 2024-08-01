package com.serch.server.admin.services.auth.requests;

import com.serch.server.services.auth.requests.RequestDevice;
import lombok.Data;

@Data
public class FinishAdminSetupRequest {
    private String password;
    private String secret;
    private String state;
    private String country;
    private RequestDevice device;
}