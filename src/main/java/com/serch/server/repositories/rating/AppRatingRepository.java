package com.serch.server.repositories.rating;

import com.serch.server.models.rating.AppRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface AppRatingRepository extends JpaRepository<AppRating, Long> {
    Optional<AppRating> findByAccount(@NonNull String rater);
}