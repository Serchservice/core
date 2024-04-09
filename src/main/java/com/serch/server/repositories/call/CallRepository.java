package com.serch.server.repositories.call;

import com.serch.server.models.conversation.call.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CallRepository extends JpaRepository<Call, String> {
    @Query("SELECT c from calls c where (c.called.serchId = :userId OR c.caller.serchId = :userId)" +
            "order by c.createdAt desc"
    )
    List<Call> findBySerchId(@Param("userId") UUID userId);
}