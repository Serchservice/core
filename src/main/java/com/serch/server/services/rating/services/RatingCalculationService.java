package com.serch.server.services.rating.services;

import com.serch.server.services.rating.requests.RatingCalculation;

import java.util.List;

public interface RatingCalculationService {
    double getUpdatedRating(List<RatingCalculation> ratings, Double avg);
    double calculateWeightedAverage(List<RatingCalculation> ratings);
    double calculateWeight(long daysOld);
}
