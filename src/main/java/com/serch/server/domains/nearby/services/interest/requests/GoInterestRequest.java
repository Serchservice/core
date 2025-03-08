package com.serch.server.domains.nearby.services.interest.requests;

import lombok.Data;

import java.util.List;

@Data
public class GoInterestRequest {
    private List<Long> interests;
}