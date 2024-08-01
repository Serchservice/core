package com.serch.server.admin.services.auth.requests;

import com.serch.server.services.auth.requests.RequestDevice;
import lombok.Data;

@Data
public class AdminResetPasswordRequest {
    private String token;
    private String password;
    private String state;
    private String country;
    private RequestDevice device;
}
