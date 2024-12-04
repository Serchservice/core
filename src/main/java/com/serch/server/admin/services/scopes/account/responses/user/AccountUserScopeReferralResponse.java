package com.serch.server.admin.services.scopes.account.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class AccountUserScopeReferralResponse {
    private UUID id;

    @JsonProperty("referral_code")
    private String referralCode;

    @JsonProperty("referral_link")
    private String referralLink;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;

    private List<Referral> referrals = new ArrayList<>();

    @Data
    public static class Referral {
        private String id;
        private CommonProfileResponse profile;

        @JsonProperty("created_at")
        private ZonedDateTime createdAt;

        @JsonProperty("updated_at")
        private ZonedDateTime updatedAt;
    }
}
