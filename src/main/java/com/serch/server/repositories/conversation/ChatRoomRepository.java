package com.serch.server.repositories.conversation;

import com.serch.server.models.conversation.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    @Query("select c from chat_rooms c where c.creator.id = ?1 or c.roommate.id = ?1 or c.creator.business.id = ?1 or c.roommate.business.id = ?1")
    List<ChatRoom> findByUserId(@NonNull UUID id);
}