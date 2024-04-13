package com.serch.server.services.rating.responses;

import lombok.Data;

@Data
public class RatingChartResponse {
    private String month;
    private Double average;
    private Double total;
    private Double bad;
    private Double good;
}
