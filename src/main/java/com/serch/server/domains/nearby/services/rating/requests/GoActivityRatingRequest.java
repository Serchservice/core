package com.serch.server.domains.nearby.services.rating.requests;

import lombok.Data;

@Data
public class GoActivityRatingRequest {
    private String id;
    private Double rating;
}