package com.serch.server.services.account.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.ActivityType;
import com.serch.server.enums.account.SerchCategory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestData {
    private String id;
    private ActivityType type;
    private String name;
    private String avatar;
    private SerchCategory category;
    private String status;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("invited_provider_name")
    private String invitedProviderName;

    @JsonProperty("invited_provider_id")
    private String invitedProviderId;

    @JsonProperty("invited_provider_avatar")
    private String invitedProviderAvatar;

    @JsonProperty("invited_provider_category")
    private SerchCategory invitedProviderCategory;

    @JsonProperty("requested_at")
    private String requestedAt;

    @JsonProperty("accepted_at")
    private String acceptedAt;

    @JsonProperty("arrived_at")
    private String arrivedAt;

    @JsonProperty("authorized_at")
    private String authorizedAt;

    @JsonProperty("invited_authorized_at")
    private String invitedAuthorizedAt;

    @JsonProperty("declined_at")
    private String declinedAt;

    @JsonProperty("invited_at")
    private String invitedAt;

    @JsonProperty("left_member")
    private String leftMember;

    @JsonProperty("left_at")
    private String leftAt;

    @JsonProperty("ended_at")
    private String endedAt;

    @JsonProperty("amount_spent")
    private String amountSpent;

    @JsonProperty("used_authorization")
    private Boolean usedAuthorization;

    @JsonProperty("can_cancel")
    private Boolean canCancel = false;

    @JsonProperty("can_cancel_invite")
    private Boolean canCancelInvite = false;

    @JsonProperty("can_leave")
    private Boolean canLeave = false;

    @JsonProperty("can_decline")
    private Boolean canDecline = false;

    @JsonProperty("can_decline_invite")
    private Boolean canDeclineInvite = false;

    @JsonProperty("can_accept")
    private Boolean canAccept = false;

    @JsonProperty("can_accept_invite")
    private Boolean canAcceptInvite = false;

    @JsonProperty("can_invite")
    private Boolean canInvite = false;

    @JsonProperty("can_end")
    private Boolean canEnd = false;

    private String label;

    @JsonProperty("date_time")
    private LocalDateTime dateTime;
}