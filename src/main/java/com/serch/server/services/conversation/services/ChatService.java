package com.serch.server.services.conversation.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.conversation.responses.ChatRoomResponse;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    /**
     * This will get all the chat rooms the logged-in user belongs to
     *
     * @return {@link ApiResponse} list of {@link ChatRoomResponse}
     */
    ApiResponse<List<ChatRoomResponse>> rooms();

    /**
     * This will get the room and chat messages for the given room id
     *
     * @param roomId The chat room id
     *
     * @return {@link ApiResponse} of {@link ChatRoomResponse}
     */
    ApiResponse<ChatRoomResponse> messages(String roomId);

    /**
     * This will either create and get the chat room the roommate and the logged-in user belongs to,
     * or it will create a new chat room and return the created chat room
     *
     * @param roommate The UUID of the roommate to be joined to the room or who belongs to the room
     *
     * @return {@link ApiResponse} of {@link ChatRoomResponse}
     */
    ApiResponse<ChatRoomResponse> room(UUID roommate);
}