package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.activity.GoActivityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface GoActivityCommentRepository extends JpaRepository<GoActivityComment, Long> {
  @Query("select g from GoActivityComment g where g.user.id = ?1 and g.activity.id = ?2")
  Optional<GoActivityComment> findByUserAndActivity(@NonNull UUID id, @NonNull String id1);

  Page<GoActivityComment> findByActivity_Id(@NonNull String id, Pageable pageable);

  @Query("select (count(g) > 0) from GoActivityComment g where g.activity.id = ?1")
  boolean existsByActivity_Id(@NonNull String id);

  @Query("select count(g) from GoActivityComment g where g.activity.id = ?1")
  long countByActivity_Id(@NonNull String id);

  @Transactional
  @Modifying
  @Query("delete from GoActivityComment g where g.activity.id = ?1")
  void deleteByActivity(@NonNull String activity);
}