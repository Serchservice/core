package com.serch.server.services.conversation.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.chat.MessageState;
import com.serch.server.exceptions.conversation.ChatException;
import com.serch.server.mappers.ConversationMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.conversation.ChatMessage;
import com.serch.server.models.conversation.ChatRoom;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.conversation.ChatMessageRepository;
import com.serch.server.repositories.conversation.ChatRoomRepository;
import com.serch.server.services.conversation.responses.*;
import com.serch.server.utils.ChatUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.serch.server.enums.account.SerchCategory.USER;

@Service
@RequiredArgsConstructor
public class ChatImplementation implements ChatService {
    private final ChattingService service;
    private final UserUtil userUtil;
    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<List<ChatRoomResponse>> rooms() {
        List<ChatRoom> rooms = chatRoomRepository.findByUserId(userUtil.getUser().getId());
        if(rooms == null || rooms.isEmpty()) {
            return new ApiResponse<>(List.of());
        } else {
            return new ApiResponse<>(
                    rooms.stream()
                            .filter(room -> room.getMessages() != null && !room.getMessages().isEmpty())
                            .filter(room -> !room.getMessages().stream().allMatch(msg -> msg.getState() == MessageState.DELETED))
                            .map(this::response)
                            .sorted(Comparator.comparing(ChatRoomResponse::getSentAt))
                            .toList()
            );
        }
    }

    @Override
    public ApiResponse<ChatRoomResponse> messages(String roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException("Chat not found", String.valueOf(userUtil.getUser().getId())));
        return new ApiResponse<>(response(room));
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
        return new ApiResponse<>(response(room));
    }

    private boolean isCurrentUser(UUID id) {
        return userUtil.getUser().getId().equals(id);
    }

    private ChatRoomResponse response(ChatRoom room) {
        ChatRoomResponse response = new ChatRoomResponse();

        response.setAvatar(
                isCurrentUser(room.getCreator())
                        ? profileRepository.findById(room.getRoommate()).map(Profile::getAvatar).orElse("")
                        : profileRepository.findById(room.getCreator()).map(Profile::getAvatar).orElse("")
        );
        response.setCategory(
                isCurrentUser(room.getCreator())
                        ? profileRepository.findById(room.getRoommate()).map(Profile::getCategory).orElse(USER).getType()
                        : profileRepository.findById(room.getCreator()).map(Profile::getCategory).orElse(USER).getType()
        );
        response.setImage(
                isCurrentUser(room.getCreator())
                        ? profileRepository.findById(room.getRoommate()).map(Profile::getCategory).orElse(USER).getImage()
                        : profileRepository.findById(room.getCreator()).map(Profile::getCategory).orElse(USER).getImage()
        );
        response.setLastSeen(
                isCurrentUser(room.getCreator())
                        ? TimeUtil.formatLastSignedIn(userRepository.findById(room.getRoommate()).map(User::getLastSignedIn).orElse(LocalDateTime.now()), false)
                        : TimeUtil.formatLastSignedIn(userRepository.findById(room.getCreator()).map(User::getLastSignedIn).orElse(LocalDateTime.now()), false)
        );
        response.setName(
                isCurrentUser(room.getCreator())
                        ? profileRepository.findById(room.getRoommate()).map(Profile::getFullName).orElse("")
                        : profileRepository.findById(room.getCreator()).map(Profile::getFullName).orElse("")
        );
        response.setRoommate(isCurrentUser(room.getCreator()) ? room.getRoommate() : room.getCreator());
        response.setUser(userUtil.getUser().getId());
        response.setRoom(room.getId());

        ChatMessage message = service.getLastMessage(room.getMessages());
        if(message != null) {
            response.setLabel(TimeUtil.formatDay(message.getCreatedAt()));
            response.setStatus(message.getStatus());
            response.setMessage(ChatUtil.formatRoomMessage(message.getMessage(), message.getType(), isCurrentUser(message.getSender()), response.getName()));
            response.setSentAt(message.getCreatedAt());
        }
        response.setCount(chatMessageRepository.countMessagesReceivedByUser(room.getId(), userUtil.getUser().getId()));
        response.setGroups(response(room.getMessages()));
        return service.updateResponse(room, response);
    }

    private List<ChatGroupMessageResponse> response(List<ChatMessage> messages) {
        if(messages == null || messages.isEmpty()) {
            return new ArrayList<>();
        } else {
            // Group messages by the date they were sent
            messages.removeIf((message) -> message.getState() != MessageState.ACTIVE);
            if(messages.isEmpty()) {
                return new ArrayList<>();
            } else {
                Map<LocalDate, List<ChatMessage>> messagesByDate = messages.stream()
                        .collect(Collectors.groupingBy(message -> message.getCreatedAt().toLocalDate()));

                List<ChatGroupMessageResponse> response = new ArrayList<>();
                messagesByDate.forEach((date, chats) -> {
                    ChatGroupMessageResponse chat = new ChatGroupMessageResponse();
                    chat.setLabel(TimeUtil.formatChatLabel(LocalDateTime.of(date, LocalTime.now())));
                    chat.setTime(LocalDateTime.of(date, LocalTime.now()));

                    List<ChatMessageResponse> messageList = chats.stream()
                            .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                            .map(this::response)
                            .collect(Collectors.toList());
                    chat.setMessages(messageList);
                    response.add(chat);
                });
                response.sort(Comparator.comparing(ChatGroupMessageResponse::getTime));
                return response;
            }
        }
    }

    private ChatMessageResponse response(ChatMessage message) {
        ChatMessageResponse response = ConversationMapper.INSTANCE.response(message);
        response.setLabel(TimeUtil.formatTime(message.getCreatedAt()));
        response.setHasOnlyEmojis(ChatUtil.containsOnlyEmojis(message.getMessage()));
        response.setHasOnlyOneEmoji(ChatUtil.containsOnlyOneEmoji(message.getMessage()));
        response.setIsSentByCurrentUser(isCurrentUser(message.getSender()));
        response.setReply(replied(message.getReplied()));
        response.setName(profileRepository.findById(message.getSender()).map(Profile::getFullName).orElse(""));
        response.setRoom(message.getChatRoom().getId());
        return response;
    }

    private ChatReplyResponse replied(ChatMessage message) {
        if(message != null) {
            ChatReplyResponse response = ConversationMapper.INSTANCE.reply(message);
            response.setMessage(ChatUtil.formatRepliedMessage(message.getMessage(), message.getDuration(), message.getType()));
            response.setLabel(TimeUtil.formatTime(message.getCreatedAt()));
            response.setHasOnlyEmojis(ChatUtil.containsOnlyEmojis(message.getMessage()));
            response.setHasOnlyOneEmoji(ChatUtil.containsOnlyOneEmoji(message.getMessage()));
            response.setIsSentByCurrentUser(isCurrentUser(message.getSender()));
            response.setSender(profileRepository.findById(message.getSender()).map(Profile::getFullName).orElse(""));
            return response;
        } else {
            return null;
        }
    }
}
