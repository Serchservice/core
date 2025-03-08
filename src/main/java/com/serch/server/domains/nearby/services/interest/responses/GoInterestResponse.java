package com.serch.server.domains.nearby.services.interest.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoInterestResponse {
    private Long id;
    private String name;
    private String verb;
    private String emoji;
    private Long popularity;
    private String title;

    @JsonProperty("nearby_popularity")
    private Long nearbyPopularity;

    @JsonProperty("category_id")
    private Long categoryId;

    private String category;
}