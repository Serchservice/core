package com.serch.server.repositories.conversation;

import com.serch.server.models.conversation.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    @Query("select c from chat_messages c where c.sender = ?1")

    List<ChatMessage> findBySender(@NonNull UUID id);

    List<ChatMessage> findByChatRoom_Id(@NonNull String id);

    @Query("SELECT COUNT(m) FROM chat_messages m WHERE m.chatRoom.id = ?1 AND m.sender != ?2  AND m.status != 'READ' and m.state = 'ACTIVE'")
    Long countMessagesReceivedByUser(String roomId, UUID senderId);

    @Query("select c from chat_messages c where c.chatRoom.id = ?1 and c.sender != ?2 and c.status = 'SENT' and c.state = 'ACTIVE'")
    List<ChatMessage> findMessagesReceivedByUser(@NonNull String id, @NonNull UUID sender);
}