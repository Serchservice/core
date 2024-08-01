package com.serch.server.repositories.auth;

import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.models.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAddressIgnoreCase(String emailAddress);
    long countByRole(@NonNull Role role);

    @Query("select count(u) from users u where u.status = ?1 and (u.role = 'ADMIN' or u.role = 'MANAGER' or u.role = 'TEAM')")
    long countAdminByStatus(@NonNull AccountStatus accountStatus);

    List<User> findByCountryLikeIgnoreCase(@NonNull String country);

    List<User> findByStateLikeIgnoreCase(@NonNull String state);
}