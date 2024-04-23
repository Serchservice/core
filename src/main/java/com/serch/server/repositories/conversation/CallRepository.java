package com.serch.server.repositories.conversation;

import com.serch.server.enums.call.CallStatus;
import com.serch.server.models.conversation.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CallRepository extends JpaRepository<Call, String> {
    @Query("SELECT c from calls c where (c.called.id = :userId OR c.caller.id = :userId OR c.called.business.id = :userId OR c.caller.business.id = :userId)" +
            "order by c.createdAt desc"
    )
    List<Call> findByUserId(@Param("userId") UUID userId);
    @Query("SELECT c from calls c where (c.called.id = :userId OR c.caller.id = :userId) and " +
            "(c.status = :status or c.status = :stats or c.status = :stat)")
    Call findActiveCall(UUID userId, CallStatus status, CallStatus stats, CallStatus stat);
}