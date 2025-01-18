package com.serch.server.domains.trip.responses;

import lombok.Data;

@Data
public class QuotationResponse {
    private Long id;
    private String amount;
    private String account;
    private String name;
    private String avatar;
    private Double rating;
    private String distance;
}