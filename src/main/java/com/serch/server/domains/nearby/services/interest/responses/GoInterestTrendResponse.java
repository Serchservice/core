package com.serch.server.domains.nearby.services.interest.responses;

import lombok.Data;

@Data
public class GoInterestTrendResponse {
    private Long interest;
    private String title;
    private String message;
}