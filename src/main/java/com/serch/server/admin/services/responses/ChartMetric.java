package com.serch.server.admin.services.responses;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChartMetric {
    private Long value;
    private BigDecimal amount = BigDecimal.ZERO;
    private String color = "";
    private String image;
    private String label;
    private List<ChartMetric> metrics = new ArrayList<>();
}