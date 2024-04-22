package com.serch.server.services.referral.responses;

import com.serch.server.enums.referral.ReferralReward;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReferralProgramData {
    private String referralCode;
    private String referLink;
    private BigDecimal credits = BigDecimal.ZERO;
    private String description;
    private Integer credit;
    private ReferralReward reward;
}
