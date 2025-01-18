package com.serch.server.domains.rating.services;

import com.serch.server.domains.rating.requests.RatingCalculation;

import java.util.List;

public interface RatingCalculationService {
    double getUpdatedRating(List<RatingCalculation> ratings, Double avg);
    double calculateWeightedAverage(List<RatingCalculation> ratings);
    double calculateWeight(long daysOld);
}
