package com.serch.server.repositories.conversation;

import com.serch.server.models.conversation.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    @Query("select c from chat_messages c where c.sender.id = ?1 or c.receiver.id = ?1 or c.sender.business.id = ?1 or c.receiver.business.id = ?1")
    List<ChatMessage> findByUserId(@NonNull UUID id);
}