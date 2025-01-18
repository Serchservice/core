package com.serch.server.domains.rating.responses;

import lombok.Data;

@Data
public class RatingResponse {
    private String name;
    private Double rating;
    private String comment;
    private String category;
    private String image;
}
