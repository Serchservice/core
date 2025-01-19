package com.serch.server.domains.conversation.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.conversation.ChatRoom;
import com.serch.server.domains.conversation.responses.ChatGroupMessageResponse;
import com.serch.server.domains.conversation.responses.ChatRoomResponse;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    /**
     * This will get all the chat rooms the logged-in user belongs to
     *
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @return {@link ApiResponse} list of {@link ChatRoomResponse}
     */
    ApiResponse<List<ChatRoomResponse>> rooms(Integer page, Integer size);

    /**
     * This will get all the chat rooms the tagged user belongs to
     *
     * @param id The user id to fetch its room list.
     * @return {@link ApiResponse} list of {@link ChatRoomResponse}
     */
    List<ChatRoomResponse> rooms(UUID id);

    /**
     * This will get the chat messages for the given room id
     *
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @param roomId The chat room id
     *
     * @return {@link ApiResponse} list of {@link ChatGroupMessageResponse}
     */
    ApiResponse<List<ChatGroupMessageResponse>> messages(String roomId, Integer page, Integer size);

    /**
     * Fetch the room response of a chat
     *
     * @param room The {@link ChatRoom} response
     * @param user The user id who is attached to the room
     * @return {@link ChatRoomResponse} response
     */
    ChatRoomResponse getChatRoomResponse(ChatRoom room, UUID user);

    /**
     * This will get the chat room details for the given room id
     *
     * @param roomId The chat room id
     *
     * @return {@link ApiResponse} list of {@link ChatRoomResponse}
     */
    ApiResponse<ChatRoomResponse> room(String roomId);

    /**
     * This will either create and get the chat room the roommate and the logged-in user belongs to,
     * or it will create a new chat room and return the created chat room
     *
     * @param roommate The UUID of the roommate to be joined to the room or who belongs to the room
     *
     * @return {@link ApiResponse} of {@link ChatRoomResponse}
     */
    ApiResponse<ChatRoomResponse> getOrCreate(UUID roommate);
}