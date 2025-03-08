package com.serch.server.domains.nearby.services.rating.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoActivityRatingResponse {
    private Long id;
    private Double rating;
    private String name;
    private String avatar;

    @JsonProperty("is_current_user")
    private Boolean isCurrentUser = false;
}