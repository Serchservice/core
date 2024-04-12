package com.serch.server.services.shared.responses;

import lombok.Data;

import java.util.List;

@Data
public class GuestResponse {
    private SharedLinkData data;
    private GuestProfileData profile;
    private SharedProfileData provider;
    private SharedProfileData user;
    private List<SharedPricingData> pricing;
}
