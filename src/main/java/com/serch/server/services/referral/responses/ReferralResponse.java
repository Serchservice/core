package com.serch.server.services.referral.responses;

import com.serch.server.enums.shared.UseStatus;
import lombok.Data;

@Data
public class ReferralResponse {
    private String name;
    private String id;
    private String avatar;
    private String role;
    private UseStatus status;
    private ReferralData data;
}
