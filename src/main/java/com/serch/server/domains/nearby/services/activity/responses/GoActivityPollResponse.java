package com.serch.server.domains.nearby.services.activity.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoActivityPollResponse {
    @JsonProperty("total_nearby_users_with_same_shared_interest")
    private Long totalNearbyUsersWithSameSharedInterest;

    @JsonProperty("total_users_with_same_shared_interest")
    private Long totalUsersWithSameSharedInterest;

    @JsonProperty("total_attending_users")
    private Long totalAttendingUsers;
}