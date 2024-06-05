package com.serch.server.services.conversation.services;

import com.serch.server.enums.chat.MessageState;
import com.serch.server.enums.chat.MessageStatus;
import com.serch.server.enums.chat.MessageType;
import com.serch.server.mappers.ConversationMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.bookmark.Bookmark;
import com.serch.server.models.conversation.ChatMessage;
import com.serch.server.models.conversation.ChatRoom;
import com.serch.server.models.schedule.Schedule;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.bookmark.BookmarkRepository;
import com.serch.server.repositories.conversation.ChatMessageRepository;
import com.serch.server.repositories.conversation.ChatRoomRepository;
import com.serch.server.repositories.schedule.ScheduleRepository;
import com.serch.server.services.conversation.requests.SendMessageRequest;
import com.serch.server.services.conversation.requests.UpdateMessageRequest;
import com.serch.server.services.conversation.responses.*;
import com.serch.server.utils.ChatUtil;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.serch.server.enums.account.SerchCategory.USER;

@Service
@RequiredArgsConstructor
public class ChattingImplementation implements ChattingService {
    private final UserUtil userUtil;
    private final SimpMessagingTemplate template;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ScheduleRepository scheduleRepository;

    private boolean isCurrentUser(UUID id) {
        return userUtil.getUser().getId().equals(id);
    }

    @Override
    public void send(SendMessageRequest request) {
        Assert.isTrue(
                ((request.getMessage() != null && !request.getMessage().isEmpty()) || !HelperUtil.isUploadEmpty(request.getFile())),
                "Message and file cannot be null"
        );
        ChatRoom room = chatRoomRepository.findById(request.getRoom()).orElse(null);

        if(room != null) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(request.getMessage());
            if(ChatUtil.hasEmojis(request.getMessage())) {
                chatMessage.setType(MessageType.EMOJI);
            } else if(ChatUtil.containsOnlyEmojis(request.getMessage())) {
                chatMessage.setType(MessageType.EMOJI);
            } else {
                chatMessage.setType(request.getType());
            }

            if(request.getReplied() != null && !request.getReplied().isEmpty()) {
                ChatMessage replied = chatMessageRepository.findById(request.getReplied()).orElse(null);
                chatMessage.setReplied(replied);
            }
            chatMessage.setChatRoom(room);
            chatMessage.setSender(userUtil.getUser().getId());
            chatMessageRepository.save(chatMessage);
            sendMessage(room);
        }
    }

    @Override
    public void update(UpdateMessageRequest request) {
        ChatMessage message = chatMessageRepository.findById(request.getId()).orElse(null);
        ChatRoom room = chatRoomRepository.findById(request.getRoom()).orElse(null);
        if(message != null && room != null) {
            if(request.getState() != null && isCurrentUser(message.getSender())) {
                message.setState(MessageState.DELETED);
                message.setUpdatedAt(LocalDateTime.now());
                sendMessage(room);
            } else if(!isCurrentUser(message.getSender())) {
                message.setStatus(request.getStatus());
                message.setUpdatedAt(LocalDateTime.now());
                sendMessage(room);
            }
        }
    }

    public void sendMessage(ChatRoom room) {
        ChatRoomResponse myResponse =  getChatRoomResponse(
                room, isCurrentUser(room.getCreator()) ? room.getRoommate() : room.getCreator(),
                userUtil.getUser().getId()
        );
        template.convertAndSend(
                "/room/%s/%s".formatted(room.getId(), String.valueOf(userUtil.getUser().getId())),
                myResponse
        );
        ChatRoomResponse receiverResponse =  getChatRoomResponse(
                room, userUtil.getUser().getId(),
                isCurrentUser(room.getCreator()) ? room.getRoommate() : room.getCreator()
        );
        template.convertAndSend(
                "/room/%s/%s".formatted(room.getId(), String.valueOf(isCurrentUser(room.getCreator()) ? room.getRoommate() : room.getCreator())),
                receiverResponse
        );
    }

    @Override
    public void announce(String room) {
        Profile profile = profileRepository.findById(userUtil.getUser().getId()).orElse(null);
        ChatRoom chatRoom = chatRoomRepository.findById(room).orElse(null);
        if(chatRoom != null && profile != null) {
            List<ChatMessage> messages = chatMessageRepository.findMessagesReceivedByUser(chatRoom.getId(), userUtil.getUser().getId());
            if(messages != null && !messages.isEmpty()) {
                messages.forEach(message -> {
                    message.setStatus(MessageStatus.DELIVERED);
                    message.setUpdatedAt(LocalDateTime.now());
                    chatMessageRepository.save(message);
                });
            }
            template.convertAndSend(
                    "/room/%s".formatted(String.valueOf(isCurrentUser(chatRoom.getCreator()) ? chatRoom.getRoommate() : chatRoom.getCreator())),
                    "%s is now online".formatted(profile.getFirstName())
            );
            sendMessage(chatRoom);
        }
    }

    private ChatRoomResponse getChatRoomResponse(ChatRoom room, UUID id, UUID count) {
        List<ChatMessage> roomMessages = chatMessageRepository.findByChatRoom_Id(room.getId());
        List<ChatMessage> messages = roomMessages != null && !roomMessages.isEmpty()
                ? roomMessages : new ArrayList<>();
        room.setMessages(messages);
        return response(room, id, count);
    }

    private ChatRoomResponse response(ChatRoom room, UUID id, UUID count) {
        ChatRoomResponse response = new ChatRoomResponse();

        response.setAvatar(profileRepository.findById(id).map(Profile::getAvatar).orElse(""));
        response.setCategory(profileRepository.findById(id).map(Profile::getCategory).orElse(USER).getType());
        response.setImage(profileRepository.findById(id).map(Profile::getCategory).orElse(USER).getImage());
        response.setLastSeen(TimeUtil.formatLastSignedIn(userRepository.findById(id).map(User::getLastSignedIn).orElse(LocalDateTime.now()), false));
        response.setName(profileRepository.findById(id).map(Profile::getFullName).orElse(""));
        response.setRoommate(id);
        response.setUser(id);
        response.setRoom(room.getId());

        ChatMessage message = getLastMessage(room.getMessages());
        if(message != null) {
            response.setLabel(TimeUtil.formatDay(message.getCreatedAt()));
            response.setStatus(message.getStatus());
            response.setMessage(ChatUtil.formatRoomMessage(message.getMessage(), message.getType(), !message.getSender().equals(id), response.getName()));
            response.setSentAt(message.getCreatedAt());
        }
        response.setCount(chatMessageRepository.countMessagesReceivedByUser(room.getId(), count));
        response.setGroups(response(room.getMessages(), id));
        return updateResponse(room, response);
    }

    @Override
    public ChatRoomResponse updateResponse(ChatRoom room, ChatRoomResponse response) {
        response.setIsBookmarked(
                bookmarkRepository.existsByUser_IdAndProvider_Id(room.getRoommate(), room.getCreator())
                        || bookmarkRepository.existsByUser_IdAndProvider_Id(room.getCreator(), room.getRoommate())
        );
        response.setBookmark(
                bookmarkRepository.findByUser_IdAndProvider_Id(room.getRoommate(), room.getCreator())
                        .map(Bookmark::getBookmarkId)
                        .orElse(bookmarkRepository.findByUser_IdAndProvider_Id(room.getCreator(), room.getRoommate())
                                .map(Bookmark::getBookmarkId)
                                .orElse("")
                        )
        );
        response.setIsScheduled(
                scheduleRepository.existByUserAndProvider(room.getRoommate(), room.getCreator())
                        || scheduleRepository.existByUserAndProvider(room.getCreator(), room.getRoommate())
        );
        response.setSchedule(
                scheduleRepository.findByUserAndProvider(room.getRoommate(), room.getCreator())
                        .map(Schedule::getId)
                        .orElse(scheduleRepository.findByUserAndProvider(room.getCreator(), room.getRoommate())
                                .map(Schedule::getId)
                                .orElse("")
                        )

        );
        return response;
    }

    private List<ChatGroupMessageResponse> response(List<ChatMessage> messages, UUID id) {
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
                            .map(msg -> response(msg, id))
                            .collect(Collectors.toList());
                    chat.setMessages(messageList);
                    response.add(chat);
                });
                response.sort(Comparator.comparing(ChatGroupMessageResponse::getTime));
                return response;
            }
        }
    }

    private ChatMessageResponse response(ChatMessage message, UUID id) {
        ChatMessageResponse response = ConversationMapper.INSTANCE.response(message);
        response.setLabel(TimeUtil.formatTime(message.getCreatedAt()));
        response.setHasOnlyEmojis(ChatUtil.containsOnlyEmojis(message.getMessage()));
        response.setHasOnlyOneEmoji(ChatUtil.containsOnlyOneEmoji(message.getMessage()));
        response.setIsSentByCurrentUser(!message.getSender().equals(id));
        response.setReply(replied(message.getReplied(), id));
        response.setName(profileRepository.findById(message.getSender()).map(Profile::getFullName).orElse(""));
        response.setRoom(message.getChatRoom().getId());
        return response;
    }

    private ChatReplyResponse replied(ChatMessage message, UUID id) {
        if(message != null) {
            ChatReplyResponse response = ConversationMapper.INSTANCE.reply(message);
            response.setMessage(ChatUtil.formatRepliedMessage(message.getMessage(), message.getDuration(), message.getType()));
            response.setLabel(TimeUtil.formatTime(message.getCreatedAt()));
            response.setHasOnlyEmojis(ChatUtil.containsOnlyEmojis(message.getMessage()));
            response.setHasOnlyOneEmoji(ChatUtil.containsOnlyOneEmoji(message.getMessage()));
            response.setIsSentByCurrentUser(!message.getSender().equals(id));
            response.setSender(profileRepository.findById(message.getSender()).map(Profile::getFullName).orElse(""));
            return response;
        } else {
            return null;
        }
    }

    @Override
    public ChatMessage getLastMessage(List<ChatMessage> messages) {
        if(messages == null || messages.isEmpty()) {
            return null;
        } else {
            messages.removeIf(chatMessage -> chatMessage.getState() != MessageState.ACTIVE);
            if(messages.isEmpty()) {
                return null;
            }
//        return Collections.max(messages, (a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
            return Collections.max(messages, Comparator.comparing(ChatMessage::getCreatedAt));
        }
    }
}
