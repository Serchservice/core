package com.serch.server.domains.rating.requests;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingCalculation {
    private LocalDateTime createdAt;
    private Double rating;
}
