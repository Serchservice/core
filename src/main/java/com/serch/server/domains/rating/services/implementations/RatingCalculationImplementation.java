package com.serch.server.domains.rating.services.implementations;

import com.serch.server.domains.rating.requests.RatingCalculation;
import com.serch.server.domains.rating.services.RatingCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingCalculationImplementation implements RatingCalculationService {
    @Value("${application.account.rating.limit}")
    private Integer ACCOUNT_MIN_RATING_LIMIT;

    @Override
    public double getUpdatedRating(List<RatingCalculation> ratings, Double avg) {
        double newWeightedAverage = calculateWeightedAverage(ratings);

        double finalRating;
        if (ratings.size() < ACCOUNT_MIN_RATING_LIMIT) {
            // Blend the new weighted average with the initial rating of 5.0
            double initialRating = 5.0;
            double blendFactor = (double) ratings.size() / ACCOUNT_MIN_RATING_LIMIT;
            finalRating = initialRating * (1 - blendFactor) + newWeightedAverage * blendFactor;
        } else {
            finalRating = newWeightedAverage;
        }

        // Cap the maximum decrease from the previous rating
        finalRating = capRatingDecrease(finalRating, avg != null ? avg : 5.0);

        // Round the final rating to two decimal places
        BigDecimal roundedRating = BigDecimal.valueOf(finalRating).setScale(2, RoundingMode.HALF_UP);

        return roundedRating.doubleValue();
    }

    private double capRatingDecrease(double newRating, double previousRating) {
        double maxDecrease = 0.5; // Set the maximum allowable decrease
        return Math.max(newRating, previousRating - maxDecrease);
    }


    @Override
    public double calculateWeightedAverage(List<RatingCalculation> ratings) {
        double totalWeightedRating = 0.0;
        double totalWeight = 0.0;
        LocalDateTime currentTime = LocalDateTime.now();

        for (RatingCalculation rating : ratings) {
            long daysOld = ChronoUnit.DAYS.between(rating.getCreatedAt(), currentTime);
            double weight = calculateWeight(daysOld);
            totalWeightedRating += rating.getRating() * weight;
            totalWeight += weight;
        }

        return totalWeight == 0 ? 0 : totalWeightedRating / totalWeight;
    }

    @Override
    public double calculateWeight(long daysOld) {
        double decayRate = 0.95;
        return Math.pow(decayRate, daysOld);
    }
}
