package com.serch.server.domains.nearby.services.account.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.domains.nearby.bases.GoBaseUserResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GoAccountResponse extends GoBaseUserResponse {
    private String id;

    @JsonProperty("search_radius")
    private Double searchRadius;

    @JsonProperty("email_confirmed_at")
    private String emailConfirmedAt;

    @JsonProperty("joined_on")
    private String joinedOn;

    private String timezone;
    private GoLocationResponse location;
}
