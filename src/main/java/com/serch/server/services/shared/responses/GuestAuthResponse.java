package com.serch.server.services.shared.responses;

import lombok.Data;

@Data
public class GuestAuthResponse {
    private SharedLinkData data;
    private SharedProfileData provider;
    private SharedProfileData user;
}
