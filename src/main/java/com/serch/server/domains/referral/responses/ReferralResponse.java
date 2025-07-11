package com.serch.server.domains.referral.responses;

import lombok.Data;

@Data
public class ReferralResponse {
    private String referId;
    private String name;
    private String avatar;
    private String role;
    private String info;
}