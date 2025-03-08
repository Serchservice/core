package com.serch.server.domains.nearby.services.interest.responses;

import lombok.Data;

import java.util.List;

@Data
public class GoInterestUpdateResponse {
    private List<GoInterestCategoryResponse> taken;
    private List<GoInterestCategoryResponse> reserved;
}