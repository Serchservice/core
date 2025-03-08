package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.activity.GoActivityRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface GoActivityRatingRepository extends JpaRepository<GoActivityRating, Long> {
  @Transactional
  @Modifying
  @Query("delete from GoActivityRating g where g.activity.id = ?1")
  void deleteByActivity(@NonNull String activity);

  @Query("select g from GoActivityRating g where g.user.id = ?1 and g.activity.id = ?2")
  Optional<GoActivityRating> findByUserAndActivity(@NonNull UUID id, @NonNull String id1);

  @Query("SELECT COALESCE(AVG(r.rating), 0) FROM GoActivityRating r WHERE r.activity.id = :activity")
  double findAverageRatingByActivity(String activity);

  @Query("select (count(g) > 0) from GoActivityRating g where g.activity.id = ?1")
  boolean existsByActivity_Id(@NonNull String id);

  @Query("select count(g) from GoActivityRating g where g.activity.id = ?1")
  long countByActivity_Id(@NonNull String id);

  Page<GoActivityRating> findByActivity_Id(String id, Pageable pageable);
}