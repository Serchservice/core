package com.serch.server.services.conversation.services;

import com.serch.server.models.conversation.ChatMessage;
import com.serch.server.models.conversation.ChatRoom;
import com.serch.server.services.conversation.requests.SendMessageRequest;
import com.serch.server.services.conversation.requests.UpdateMessageRequest;
import com.serch.server.services.conversation.responses.ChatRoomResponse;

import java.util.List;
import java.util.UUID;

public interface ChattingService {
    /**
     * Sends a chat message to the receiver and also return the update to the sender
     *
     * @param request The {@link SendMessageRequest} request data
     */
    void send(SendMessageRequest request);

    /**
     * Sends an update to the chat room to show latest update
     *
     * @param roomId The room id
     */
    void refresh(String roomId);

    /**
     * Update a particular message in the chat room. Can be to delete, mark as read or delivered
     *
     * @param request The {@link UpdateMessageRequest} request data
     */
    void update(UpdateMessageRequest request);

    /**
     * Update a particular message in the chat room. Can be to delete, mark as read or delivered
     *
     * @param request The {@link UpdateMessageRequest} request data
     */
    void updateAll(UpdateMessageRequest request);

    /**
     * Announce the presence of a roommate
     *
     * @param room The room id
     */
    void announce(String room);

    /**
     * Update some fields in the Chat room response
     *
     * @param room The {@link ChatRoom} data to complete response from.
     * @param response The {@link ChatRoomResponse} to update its fields
     * @param isProvider Checks if the response being prepared is for a provider or associate provider account
     *
     * @return Updated {@link ChatRoomResponse} data
     */
    ChatRoomResponse updateResponse(ChatRoom room, ChatRoomResponse response, boolean isProvider);

    /**
     * This will get the last message sent from the list of messages sent it.
     * Fetches the data by the last <code>createdAt</code> date
     *
     * @param messages A list of {@link ChatMessage} messages
     *
     * @return The latest {@link ChatMessage}
     */
    ChatMessage getLastMessage(List<ChatMessage> messages);

    /**
     * This will prepare a chat room response with the given room data.
     *
     * @param room The {@link ChatRoom} to prepare the data from.
     *
     * @return A transformed {@link ChatRoomResponse} data
     */
    ChatRoomResponse response(ChatRoom room);

    /**
     * This will fetch the list of chat rooms the user has opened or is part of.
     * It will return the list by current date and/or where the user (as a roommate or creator) was bookmarked
     *
     * @param id The id of the user whose chat history is being requested
     *
     * @return The List of {@link ChatRoomResponse}
     */
    List<ChatRoomResponse> response(UUID id);

    /**
     * This will clear old chats at the start of the new day, if the provider is not bookmarked
     */
    void clearChats();
}
