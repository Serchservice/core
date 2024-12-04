package com.serch.server.repositories.account;

import com.serch.server.models.account.PhoneInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhoneInformationRepository extends JpaRepository<PhoneInformation, Long> {
    Optional<PhoneInformation> findByUser_Id(@NonNull UUID id);

    @Query("select p from PhoneInformation p where p.phoneNumber = ?1 and (p.user.role = 'PROVIDER' or p.user.role = 'ASSOCIATE_PROVIDER') ")
    Page<PhoneInformation> findByPhoneNumber(@NonNull String phoneNumber, Pageable pageable);
}