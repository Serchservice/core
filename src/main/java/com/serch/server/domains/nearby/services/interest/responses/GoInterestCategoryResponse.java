package com.serch.server.domains.nearby.services.interest.responses;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GoInterestCategoryResponse {
    private Long id;
    private String name;
    private List<GoInterestResponse> interests = new ArrayList<>();
}