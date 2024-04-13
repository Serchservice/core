package com.serch.server.services.rating.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.rating.requests.RateAppRequest;
import com.serch.server.services.rating.requests.RateRequest;
import com.serch.server.services.rating.responses.RatingChartResponse;
import com.serch.server.services.rating.responses.RatingResponse;

import java.util.List;

public interface RatingService {
    ApiResponse<String> rate(RateRequest request);
    ApiResponse<String> share(RateRequest request);
    ApiResponse<Double> rate(RateAppRequest request);
    ApiResponse<List<RatingResponse>> view();
    ApiResponse<List<RatingResponse>> good();
    ApiResponse<List<RatingResponse>> bad();
    ApiResponse<List<RatingChartResponse>> chart();
    ApiResponse<Double> app(String id);
}
