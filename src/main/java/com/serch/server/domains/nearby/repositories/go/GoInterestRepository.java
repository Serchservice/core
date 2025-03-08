package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.interest.GoInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;

public interface GoInterestRepository extends JpaRepository<GoInterest, Long> {
    @Query("select i from GoInterest i where i.id not in ?1")
    List<GoInterest> findReserved(@NonNull Collection<Long> ids);
}