package com.serch.server.repositories.conversation;

import com.serch.server.models.conversation.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    @Query("select c from chat_rooms c where c.creator = ?1 or c.roommate = ?1")
    List<ChatRoom> findByUserId(@NonNull UUID id);

    @Query("select c from chat_rooms c where c.creator = ?1 or c.roommate = ?1")
    Page<ChatRoom> findByUserId(@NonNull UUID id, Pageable pageable);

    @Query("select count(c) from chat_rooms c where c.creator = ?1 or c.roommate = ?1 and c.createdAt between ?2 and ?3")
    long countByAccountAndDate(@NonNull UUID id, ZonedDateTime start, ZonedDateTime end);

    Optional<ChatRoom> findByRoommateAndCreator(@NonNull UUID id, @NonNull UUID creator);
}