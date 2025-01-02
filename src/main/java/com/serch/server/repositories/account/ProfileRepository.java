package com.serch.server.repositories.account;

import com.serch.server.enums.auth.Role;
import com.serch.server.models.account.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByUser_EmailAddress(@NonNull String emailAddress);

    @Query("select b from Profile b where b.createdAt between ?1 and ?2")
    List<Profile> findByCreatedAtBetween(ZonedDateTime startMonth, ZonedDateTime endMonth);

    @Query("select b from Profile b where b.user.role = ?1 and b.createdAt between ?2 and ?3")
    List<Profile> findByRoleAndCreatedAtBetween(Role role, ZonedDateTime startMonth, ZonedDateTime endMonth);

    @Query("select p from Profile p where p.business.id = ?1 and p.user.status != 'BUSINESS_DELETED'")
    Page<Profile> findActiveAssociatesByBusinessId(@NonNull UUID id, Pageable pageable);

    @Query("""
      select p from Profile p where p.business.id = :id and p.user.status != 'BUSINESS_DELETED'
      and (
          lower(p.user.firstName) like lower(concat('%', :q, '%')) or lower(p.user.lastName) like lower(concat('%', :q, '%'))\s
          or lower(p.category) like lower(concat('%', :q, '%')) or lower(p.user.status) like lower(concat('%', :q, '%'))\s
          or lower(p.user.emailAddress) like lower(concat('%', :q, '%'))
      )
    """)
    Page<Profile> searchActiveAssociates(@NonNull UUID id, @NonNull String q, Pageable pageable);
}