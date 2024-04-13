package com.serch.server.repositories.rating;

import com.serch.server.models.rating.Rating;
import com.serch.server.services.rating.responses.RatingChartResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRated(@NonNull String rated);
    @Query("SELECT r FROM Rating r WHERE r.rating < 3.0 and r.rated = :id")
    List<Rating> findBad(String id);
    @Query("SELECT r FROM Rating r WHERE r.rating >= 3.0 and r.rated = :id")
    List<Rating> findGood(String id);
    @Query(nativeQuery = true, value =
            "SELECT " +
                    "    TO_CHAR(r.created_at, 'Mon') AS month, " +
                    "    AVG(CASE WHEN r.rating < 3.0 THEN r.rating END) AS bad, " +
                    "    AVG(CASE WHEN r.rating >= 3.0 THEN r.rating END) AS good, " +
                    "    AVG(r.rating) AS average, " +
                    "    SUM(r.rating) AS total " +
                    "FROM " +
                    "    platform.ratings r " +
                    "WHERE r.rated = :id" +
                    "    AND EXTRACT(YEAR FROM r.created_at) = EXTRACT(YEAR FROM CURRENT_DATE) " +
                    "    AND EXTRACT(MONTH FROM r.created_at) IN " +
                    "        (EXTRACT(MONTH FROM CURRENT_DATE) - 2, EXTRACT(MONTH FROM CURRENT_DATE) - 1, EXTRACT(MONTH FROM CURRENT_DATE)) " +
                    "GROUP BY " +
                    "    TO_CHAR(r.created_at, 'Mon'), " +
                    "    EXTRACT(MONTH FROM r.created_at) " +
                    "ORDER BY " +
                    "    EXTRACT(MONTH FROM r.created_at)"
    )
    List<RatingChartResponse> chart(String id);
    @Query("SELECT AVG(r.rating) FROM Rating r " +
            "WHERE r.rated = :id " +
            "AND FUNCTION('DATE', r.createdAt) = FUNCTION('CURRENT_DATE')")
    Double todayAverage(@Param("id") String id);
}