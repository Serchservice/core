package com.serch.server.repositories.conversation;

import com.serch.server.models.conversation.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    @Query("select c from chat_messages c where c.sender = ?1")
    List<ChatMessage> findBySender(@NonNull UUID id);

    @Query("select c from chat_messages c where c.chatRoom.id = ?1 and c.state = 'ACTIVE' and c.sender != ?2 and (c.status = 'DELIVERED' or c.status = 'SENT')")
    List<ChatMessage> findUnreadMessages(@NonNull String id, @NonNull UUID userId);

    @Query("select c from chat_messages c where c.chatRoom.id = ?1 and c.state = 'ACTIVE'")
    List<ChatMessage> findActiveMessages(@NonNull String id);

    @Query("select c from chat_messages c where c.chatRoom.id = ?1 and c.state = 'ACTIVE' order by c.updatedAt desc")
    Page<ChatMessage> findActiveMessages(@NonNull String id, Pageable pageable);

    @Query("SELECT COUNT(m) FROM chat_messages m WHERE m.chatRoom.id = ?1 AND m.sender != ?2 AND m.status != 'READ' and m.state = 'ACTIVE'")
    Long countMessagesReceivedByUser(String roomId, UUID senderId);

    @Query("SELECT COUNT(m) FROM chat_messages m WHERE m.sender = ?1 and m.createdAt between ?2 and ?3")
    Long countMessagesSentByUser(UUID senderId, ZonedDateTime start, ZonedDateTime end);

    @Query("select c from chat_messages c where c.chatRoom.id = ?1 and c.sender != ?2 and c.status = 'SENT' and c.state = 'ACTIVE'")
    List<ChatMessage> findMessagesReceivedByUser(@NonNull String id, @NonNull UUID sender);

    @Query("""
          select c from chat_messages c where not exists (
              select b from Bookmark b
              where
              (b.provider.id = c.chatRoom.roommate and b.user.id = c.chatRoom.creator)
              or
              (b.provider.id = c.chatRoom.creator and b.user.id = c.chatRoom.roommate)
          )
          and c.createdAt < CURRENT_TIMESTAMP
    """)
    List<ChatMessage> findAllPastMessagesWithoutBookmarkedProvider();
}