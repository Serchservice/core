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

    @Query("select c from chat_rooms c where (c.creator = ?1 or c.roommate = ?1) and (c.creator = ?2 or c.roommate = ?2)")
    Optional<ChatRoom> findRoom(@NonNull UUID id, @NonNull UUID roommate);

    @Query("""
        SELECT cr FROM chat_rooms cr
        WHERE (cr.creator = ?1 OR cr.roommate = ?1)
        AND EXISTS (SELECT msg FROM chat_messages msg WHERE msg.chatRoom.id = cr.id AND msg.state = 'ACTIVE')
        AND (FUNCTION('DATE', cr.updatedAt) >= CURRENT_DATE OR EXISTS (
          SELECT b FROM Bookmark b
          WHERE (b.user.id = cr.creator AND b.provider.id = cr.roommate)
          OR (b.user.id = cr.roommate AND b.provider.id = cr.creator)
        ))
        AND EXISTS (SELECT u FROM users u WHERE u.id = cr.creator AND u.status = 'ACTIVE')
        AND EXISTS (SELECT u FROM users u WHERE u.id = cr.roommate AND u.status = 'ACTIVE')
        ORDER BY cr.updatedAt DESC
    """)
    Page<ChatRoom> findFilteredChatRooms(UUID userId, Pageable pageable);

    @Query("""
        SELECT cr FROM chat_rooms cr
        WHERE (cr.creator = ?1 OR cr.roommate = ?1)
        AND EXISTS (SELECT msg FROM chat_messages msg WHERE msg.chatRoom.id = cr.id AND msg.state = 'ACTIVE')
        AND (FUNCTION('DATE', cr.updatedAt) >= CURRENT_DATE OR EXISTS (
          SELECT b FROM Bookmark b
          WHERE (b.user.id = cr.creator AND b.provider.id = cr.roommate)
          OR (b.user.id = cr.roommate AND b.provider.id = cr.creator)
        ))
        AND EXISTS (SELECT u FROM users u WHERE u.id = cr.creator AND u.status = 'ACTIVE')
        AND EXISTS (SELECT u FROM users u WHERE u.id = cr.roommate AND u.status = 'ACTIVE')
        ORDER BY cr.updatedAt DESC
    """)
    List<ChatRoom> findFilteredChatRooms(UUID userId);
}