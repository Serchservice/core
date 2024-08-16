package com.serch.server.repositories.rating;

import com.serch.server.models.rating.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRated(@NonNull String rated);

    List<Rating> findByRater(@NonNull String rater);

    @Query("SELECT r FROM Rating r WHERE r.rating < 3.0 and r.rated = :id")
    List<Rating> findBad(String id);

    @Query("SELECT r FROM Rating r WHERE r.rating >= 3.0 and r.rated = :id")
    List<Rating> findGood(String id);

    @Query(nativeQuery = true, value =
            "SELECT " +
                    "    TO_CHAR(r.created_at, 'Mon') AS month, " +
                    "    COALESCE(AVG(CASE WHEN r.rating < 3.0 THEN r.rating END), 0.0) AS bad, " +
                    "    COALESCE(AVG(CASE WHEN r.rating >= 3.0 THEN r.rating END), 0.0) AS good, " +
                    "    COALESCE(AVG(r.rating), 0.0) AS average, " +
                    "    COALESCE(SUM(r.rating), 0.0) AS total " +
                    "FROM " +
                    "    platform.ratings r " +
                    "WHERE r.rated = ?1" +
                    "    AND TO_CHAR(r.created_at, 'YYYYMM') >= TO_CHAR(CURRENT_DATE - INTERVAL '2 months', 'YYYYMM')" +
                    "GROUP BY " +
                    "    TO_CHAR(r.created_at, 'Mon'), " +
                    "    TO_CHAR(r.created_at, 'YYYYMM') " +
                    "ORDER BY " +
                    "    TO_CHAR(r.created_at, 'YYYYMM')"
    )
    List<Object[]> chart(String id);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.rated = ?1 AND FUNCTION('DATE', r.createdAt) = FUNCTION('CURRENT_DATE')")
    Double todayAverage(String id);

    @Query(nativeQuery = true, value = "SELECT COALESCE(AVG(r.rating), 0.0) AS average FROM  platform.ratings r WHERE r.rated = ?1")
    Double getOverallAverageRating(String id);

    Optional<Rating> getByRated(@NonNull String rated);

    Optional<Rating> findByEventAndRated(@NonNull String event, @NonNull String rated);
}