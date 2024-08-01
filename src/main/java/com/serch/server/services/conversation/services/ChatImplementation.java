package com.serch.server.services.conversation.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.chat.MessageState;
import com.serch.server.exceptions.conversation.ChatException;
import com.serch.server.models.conversation.ChatRoom;
import com.serch.server.repositories.conversation.ChatRoomRepository;
import com.serch.server.services.conversation.responses.*;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatImplementation implements ChatService {
    private final ChattingService service;
    private final UserUtil userUtil;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public ApiResponse<List<ChatRoomResponse>> rooms() {
        return new ApiResponse<>(service.response(userUtil.getUser().getId()));
    }

    @Override
    public ApiResponse<ChatRoomResponse> messages(String roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException("Chat not found", String.valueOf(userUtil.getUser().getId())));
        return new ApiResponse<>(service.response(room));
    }

    @Override
    public ApiResponse<ChatRoomResponse> room(UUID roommate) {
        ChatRoom room = chatRoomRepository.findByRoommate(roommate)
                .orElseGet(() -> {
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setCreator(userUtil.getUser().getId());
                    newRoom.setRoommate(roommate);
                    newRoom.setState(MessageState.ACTIVE);
                    return chatRoomRepository.save(newRoom);
                });
        return new ApiResponse<>(service.response(room));
    }
}
