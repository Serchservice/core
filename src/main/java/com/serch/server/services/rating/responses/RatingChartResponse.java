package com.serch.server.services.rating.responses;

import lombok.Data;

@Data
public class RatingChartResponse {
    private String month;
    private Double average;
    private Double total;
    private Double bad;
    private Double good;

    public RatingChartResponse(String month, Double bad, Double good, Double average, Double total) {
        this.month = month;
        this.bad = bad;
        this.good = good;
        this.average = average;
        this.total = total;
    }
}
