package com.serch.server.services.referral.responses;

import lombok.Data;

@Data
public class ReferralProgramResponse {
    private String name;
    private String avatar;
    private String role;
    private String referralCode;
    private String referLink;
}