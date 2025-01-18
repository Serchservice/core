package com.serch.server.domains.trip.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String name;
    private String avatar;
    private String role;
    private String category;
    private String image;
    private Double rating;
    private String phone;
    private String bookmark;

    @JsonProperty("trip_rating")
    private Double tripRating;
}
