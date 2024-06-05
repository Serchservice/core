package com.serch.server.services.conversation.services;

import com.serch.server.models.conversation.ChatMessage;
import com.serch.server.models.conversation.ChatRoom;
import com.serch.server.services.conversation.requests.SendMessageRequest;
import com.serch.server.services.conversation.requests.UpdateMessageRequest;
import com.serch.server.services.conversation.responses.ChatRoomResponse;

import java.util.List;

public interface ChattingService {
    void send(SendMessageRequest request);
    void update(UpdateMessageRequest request);
    void announce(String room);
    ChatRoomResponse updateResponse(ChatRoom room, ChatRoomResponse response);
    ChatMessage getLastMessage(List<ChatMessage> messages);
}
