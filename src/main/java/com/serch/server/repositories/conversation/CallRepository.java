package com.serch.server.repositories.conversation;

import com.serch.server.enums.call.CallType;
import com.serch.server.models.conversation.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CallRepository extends JpaRepository<Call, String> {
    @Query("SELECT c from calls c where (c.called.id = ?1 OR c.caller.id = ?1 OR c.called.business.id = ?1 OR c.caller.business.id = ?1) and c.updatedAt = CURRENT DATE order by c.createdAt desc")
    List<Call> findByUserId(UUID userId);

    Optional<Call> findByChannelAndCalled_Id(@NonNull String channel, @NonNull UUID id);

    @Query("SELECT c from calls c where c.status = 'RINGING'")
    List<Call> findAllRinging();

    List<Call> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    @Query("select count(c) from calls c where c.type = ?1 AND c.createdAt BETWEEN ?2 AND ?3")
    long countByType(@NonNull CallType type, LocalDateTime startDate, LocalDateTime endDate);

    List<Call> findByType(@NonNull CallType type);
}