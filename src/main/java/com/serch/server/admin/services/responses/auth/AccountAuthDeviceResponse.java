package com.serch.server.admin.services.responses.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountAuthDeviceResponse {
    private String name;
    private String platform;
    private Integer count;
    private Boolean revoked;
}
