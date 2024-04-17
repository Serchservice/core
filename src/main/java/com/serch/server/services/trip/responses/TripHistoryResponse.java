package com.serch.server.services.trip.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.trip.TripConnectionStatus;
import lombok.Data;

@Data
public class TripHistoryResponse {
    private String id;
    private String name;
    private String avatar;
    private SerchCategory category;
    private String amount;

    @JsonProperty("name_of_provider")
    private String providerName;
    @JsonProperty("avatar_of_provider")
    private String providerAvatar;
    @JsonProperty("category_of_provider")
    private SerchCategory providerCategory;
    @JsonProperty("status_of_provider")
    private TripConnectionStatus providerStatus;
    @JsonProperty("provider_accepted_at")
    private String providerAcceptedAt;
    @JsonProperty("provider_verified_at")
    private String providerVerifiedAt;
    @JsonProperty("cancel_reason")
    private String reason;

    @JsonProperty("name_of_invited_provider")
    private String invitedProviderName;
    @JsonProperty("avatar_of_invited_provider")
    private String invitedProviderAvatar;
    @JsonProperty("category_of_invited_provider")
    private SerchCategory invitedProviderCategory;
    @JsonProperty("status_of_invited_provider")
    private TripConnectionStatus invitedProviderStatus;
    @JsonProperty("invited_provider_invited_at")
    private String invitedProviderInvitedAt;
    @JsonProperty("invited_provider_accepted_at")
    private String invitedProviderAcceptedAt;
    @JsonProperty("invited_provider_verified_at")
    private String invitedProviderVerifiedAt;
    @JsonProperty("invite_cancel_reason")
    private String inviteReason;

    @JsonProperty("shared_amount")
    private String sharedAmount;
    @JsonProperty("provider_shared_amount_share")
    private String providerShare;
    @JsonProperty("user_shared_amount_share")
    private String userShare;

    @JsonProperty("left_at")
    private String leftAt;
    @JsonProperty("stopped_at")
    private String stoppedAt;
    @JsonProperty("requested_at")
    private String requestedAt;
}