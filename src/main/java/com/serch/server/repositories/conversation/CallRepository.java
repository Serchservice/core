package com.serch.server.repositories.conversation;

import com.serch.server.enums.call.CallType;
import com.serch.server.models.conversation.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CallRepository extends JpaRepository<Call, String> {
    @Query("SELECT c from calls c where (c.called.id = :userId OR c.caller.id = :userId OR c.called.business.id = :userId OR c.caller.business.id = :userId)" +
            "order by c.createdAt desc"
    )
    List<Call> findByUserId(@Param("userId") UUID userId);

    Optional<Call> findByChannelAndCalled_Id(@NonNull String channel, @NonNull UUID id);

    @Query("SELECT c from calls c where c.status = 'RINGING'")
    List<Call> findAllRinging();

    List<Call> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    @Query("select count(c) from calls c where c.type = :type AND c.createdAt BETWEEN :startDate AND :endDate")
    long countByType(@NonNull CallType type, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<Call> findByType(@NonNull CallType type);
}