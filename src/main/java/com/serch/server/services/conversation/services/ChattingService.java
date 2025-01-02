package com.serch.server.services.conversation.services;

import com.serch.server.services.conversation.requests.MessageTypingRequest;
import com.serch.server.services.conversation.requests.SendMessageRequest;
import com.serch.server.services.conversation.requests.UpdateMessageRequest;

import java.util.UUID;

public interface ChattingService {
    /**
     * Sends a chat message to the receiver and also return the update to the sender
     *
     * @param emailAddress The email address attached to the session
     * @param request The {@link SendMessageRequest} request data
     */
    void send(SendMessageRequest request, String emailAddress);

    /**
     * Sends an update to the chat room to show latest update
     *
     * @param emailAddress The email address attached to the session
     * @param roomId The room id
     */
    void refresh(String roomId, String emailAddress);

    /**
     * Mark all messages sent to the user as read
     *
     * @param emailAddress The email address attached to the session
     * @param roomId The room id
     */
    void markAllAsRead(String roomId, String emailAddress);

    /**
     * Sends an update on whether the user is typing or not typing a message
     *
     * @param emailAddress The email address attached to the session
     * @param request The {@link MessageTypingRequest} request data
     */
    void notifyTyping(MessageTypingRequest request, String emailAddress);

    /**
     * Update a particular message in the chat room. Can be to delete, mark as read or delivered
     *
     * @param emailAddress The email address attached to the session
     * @param request The {@link UpdateMessageRequest} request data
     */
    void update(UpdateMessageRequest request, String emailAddress);

    /**
     * Update a particular message in the chat room. Can be to delete, mark as read or delivered
     *
     * @param emailAddress The email address attached to the session
     * @param request The {@link UpdateMessageRequest} request data
     */
    void updateAll(UpdateMessageRequest request, String emailAddress);

    /**
     * Announce the presence of a roommate
     *
     * @param emailAddress The email address attached to the session
     * @param room The room id
     */
    void announce(String room, String emailAddress);

    /**
     * Notify the room members about the schedule update
     *
     * @param roommate The roommate id to send room update to
     */
    void notifyAboutSchedule(UUID roommate);

    /**
     * This will clear old chats at the start of the new day, if the provider is not bookmarked
     */
    void clearChats();
}