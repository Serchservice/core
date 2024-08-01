package com.serch.server.admin.services.responses;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChartMetric {
    private Integer value;
    private BigDecimal amount = BigDecimal.ZERO;
    private Integer challenges = 0;
    private Integer devices = 0;
    private Integer sessions = 0;
    private Integer users = 0;
    private Integer providers = 0;
    private Integer businesses = 0;
    private Integer associates = 0;
    private Integer guests = 0;
    private String color = "";
    private String image;
    private String label;
}