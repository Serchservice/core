package com.serch.server.services.rating.responses;

import lombok.Data;

@Data
public class RatingResponse {
    private String name;
    private Double rating;
    private String comment;
    private String category;
}
