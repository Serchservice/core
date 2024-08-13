package com.serch.server.services.rating.requests;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingCalculation {
    private LocalDateTime createdAt;
    private Double rating;
}
