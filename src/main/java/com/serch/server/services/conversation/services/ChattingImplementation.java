package com.serch.server.services.conversation.services;

import com.serch.server.bases.BaseUser;
import com.serch.server.enums.chat.MessageState;
import com.serch.server.enums.chat.MessageStatus;
import com.serch.server.enums.chat.MessageType;
import com.serch.server.mappers.ConversationMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.bookmark.Bookmark;
import com.serch.server.models.conversation.ChatMessage;
import com.serch.server.models.conversation.ChatRoom;
import com.serch.server.models.trip.Active;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.bookmark.BookmarkRepository;
import com.serch.server.repositories.conversation.ChatMessageRepository;
import com.serch.server.repositories.conversation.ChatRoomRepository;
import com.serch.server.repositories.schedule.ScheduleRepository;
import com.serch.server.repositories.trip.ActiveRepository;
import com.serch.server.services.conversation.requests.SendMessageRequest;
import com.serch.server.services.conversation.requests.UpdateMessageRequest;
import com.serch.server.services.conversation.responses.*;
import com.serch.server.core.notification.core.NotificationService;
import com.serch.server.services.schedule.services.SchedulingService;
import com.serch.server.utils.ChatUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.serch.server.enums.account.SerchCategory.USER;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NESTED)
public class ChattingImplementation implements ChattingService {
    private final UserUtil userUtil;
    private final SimpMessagingTemplate template;
    private final SchedulingService schedulingService;
    private final NotificationService notificationService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ScheduleRepository scheduleRepository;
    private final ActiveRepository activeRepository;

    private boolean isCurrentUser(UUID id) {
        return userUtil.getUser().isUser(id);
    }

    @Override
    @Transactional
    public void send(SendMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(request.getRoom()).orElse(null);

        if(request.getMessage() != null && !request.getMessage().isEmpty() && room != null) {
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
            sendMessage(room, true);
        }
    }

    @Override
    @Transactional
    public void refresh(String roomId) {
        chatRoomRepository.findById(roomId).ifPresent(room -> sendMessage(room, false));
    }

    @Override
    @Transactional
    public void update(UpdateMessageRequest request) {
        ChatMessage message = chatMessageRepository.findById(request.getId()).orElse(null);
        ChatRoom room = chatRoomRepository.findById(request.getRoom()).orElse(null);
        if(message != null && room != null) {
            if(request.getState() != null && isCurrentUser(message.getSender())) {
                message.setState(MessageState.DELETED);
                message.setUpdatedAt(TimeUtil.now());
                chatMessageRepository.save(message);
                sendMessage(room, false);
            } else if(!isCurrentUser(message.getSender())) {
                message.setStatus(request.getStatus());
                message.setUpdatedAt(TimeUtil.now());
                chatMessageRepository.save(message);
                sendMessage(room, false);
            }
        }
    }

    @Override
    @Transactional
    public void updateAll(UpdateMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(request.getRoom()).orElse(null);
        if(room != null) {
            if(request.getState() != null) {
                room.getMessages().stream()
                        .filter(message -> isCurrentUser(message.getSender()))
                        .forEach(message -> {
                            message.setState(MessageState.DELETED);
                            message.setUpdatedAt(TimeUtil.now());
                            chatMessageRepository.save(message);
                        });
                sendMessage(room, false);
            } else {
                room.getMessages().stream()
                        .filter(message -> !isCurrentUser(message.getSender()))
                        .forEach(message -> {
                            message.setState(request.getState());
                            message.setUpdatedAt(TimeUtil.now());
                            chatMessageRepository.save(message);
                        });
                sendMessage(room, false);
            }
        }
    }

    @Transactional
    protected void sendMessage(ChatRoom room, boolean notify) {
        sendToSender(room);
        sendToReceiver(room, notify);
    }

    @Transactional
    protected void sendToSender(ChatRoom room) {
        ChatRoomResponse response =  getChatRoomResponse(
                room, isCurrentUser(room.getCreator()) ? room.getRoommate() : room.getCreator(),
                userUtil.getUser().getId()
        );
        template.convertAndSend("/platform/%s/%s".formatted(room.getId(), String.valueOf(userUtil.getUser().getId())), response);
        template.convertAndSend("/platform/%s".formatted(String.valueOf(userUtil.getUser().getId())), response);
    }

    @Transactional
    protected void sendToReceiver(ChatRoom room, boolean notify) {
        UUID id = isCurrentUser(room.getCreator()) ? room.getRoommate() : room.getCreator();
        ChatRoomResponse response =  getChatRoomResponse(room, userUtil.getUser().getId(), id);
        template.convertAndSend("/platform/%s/%s".formatted(room.getId(), String.valueOf(id)), response);
        template.convertAndSend("/platform/%s".formatted(String.valueOf(id)), response);

        if(notify) {
            notificationService.send(id, response);
        }
    }

    @Override
    @Transactional
    public void announce(String room) {
        Profile profile = profileRepository.findById(userUtil.getUser().getId()).orElse(null);
        ChatRoom chatRoom = chatRoomRepository.findById(room).orElse(null);
        if(chatRoom != null && profile != null) {
            List<ChatMessage> messages = chatMessageRepository.findMessagesReceivedByUser(chatRoom.getId(), userUtil.getUser().getId());
            if(messages != null && !messages.isEmpty()) {
                messages.forEach(message -> {
                    message.setStatus(MessageStatus.DELIVERED);
                    message.setUpdatedAt(TimeUtil.now());
                    chatMessageRepository.save(message);
                });
            }
            template.convertAndSend(
                    "/platform/%s".formatted(String.valueOf(isCurrentUser(chatRoom.getCreator()) ? chatRoom.getRoommate() : chatRoom.getCreator())),
                    "%s is now online".formatted(profile.getUser().getFirstName())
            );
            sendMessage(chatRoom, false);
        }
    }

    @Transactional
    protected ChatRoomResponse getChatRoomResponse(ChatRoom room, UUID id, UUID count) {
        List<ChatMessage> roomMessages = chatMessageRepository.findByChatRoom_Id(room.getId());
        List<ChatMessage> messages = roomMessages != null && !roomMessages.isEmpty()
                ? roomMessages : new ArrayList<>();
        room.setMessages(messages);
        return response(room, id, count);
    }

    @Transactional
    protected ChatRoomResponse response(ChatRoom room, UUID id, UUID count) {
        ChatRoomResponse response = new ChatRoomResponse();

        prepareRoomResponse(id, response);
        response.setUser(id);
        response.setRoom(room.getId());

        ChatMessage message = getLastMessage(room.getMessages());
        User account = profileRepository.findById(count).map(BaseUser::getUser).orElse(null);

        if(message != null) {
            response.setLabel(TimeUtil.formatDay(message.getCreatedAt(), account != null ? account.getTimezone() : ""));
            response.setStatus(message.getStatus());
            response.setMessage(ChatUtil.formatRoomMessage(message.getMessage(), message.getType(), !message.getSender().equals(id), response.getName()));
            response.setSentAt(message.getCreatedAt());
            response.setMessageId(message.getId());
        }
        response.setCount(chatMessageRepository.countMessagesReceivedByUser(room.getId(), count));
        response.setGroups(response(room.getMessages(), id));

        return updateResponse(room, response, account != null && account.isProvider());
    }

    private void prepareRoomResponse(UUID id, ChatRoomResponse response) {
        Profile profile = profileRepository.findById(id).orElse(null);

        if (profile != null) {
            response.setAvatar(profile.getAvatar());
            response.setCategory(profile.getCategory().getType());
            response.setImage(profile.getCategory().getImage());
            response.setLastSeen(TimeUtil.formatLastSignedIn(profile.getUser().getLastSignedIn(), profile.getUser().getTimezone(), false));
            response.setName(profile.getFullName());
        } else {
            response.setAvatar("");
            response.setCategory(USER.getType());
            response.setImage(USER.getImage());
            response.setLastSeen(TimeUtil.formatLastSignedIn(TimeUtil.now(), "", false));
            response.setName("");
        }

        response.setRoommate(id);
        response.setIsActive(activeRepository.findByProfile_Id(id).map(Active::isActive).orElse(false));
        response.setTrip(activeRepository.findByProfile_Id(id).map(active -> active.getStatus().getType()).orElse(""));
    }

    @Override
    @Transactional
    public ChatRoomResponse updateResponse(ChatRoom room, ChatRoomResponse response, boolean isProvider) {
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
        response.setSchedule(
                scheduleRepository.findByUserAndProvider(room.getRoommate(), room.getCreator())
                        .map(schedule -> schedulingService.response(schedule, isProvider, true))
                        .orElse(scheduleRepository.findByUserAndProvider(room.getCreator(), room.getRoommate())
                                .map(schedule -> schedulingService.response(schedule, isProvider, true))
                                .orElse(null)
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

                    String timezone = userRepository.findById(id).map(User::getTimezone).orElse("");
                    chat.setLabel(TimeUtil.formatChatLabel(LocalDateTime.of(date, LocalTime.now()), timezone));
                    chat.setTime(ZonedDateTime.of(LocalDateTime.of(date, LocalTime.now()), TimeUtil.zoneId(timezone)));

                    List<ChatMessageResponse> messageList = chats.stream()
                            .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                            .map(msg -> prepareMessageResponse(msg, id))
                            .collect(Collectors.toList());
                    chat.setMessages(messageList);
                    response.add(chat);
                });
                response.sort(Comparator.comparing(ChatGroupMessageResponse::getTime));
                return response;
            }
        }
    }

    private ChatMessageResponse prepareMessageResponse(ChatMessage message, UUID id) {
        ChatMessageResponse response = ConversationMapper.INSTANCE.response(message);
        response.setLabel(TimeUtil.formatTime(message.getCreatedAt(), profileRepository.findById(id).map(p -> p.getUser().getTimezone()).orElse("")));
        response.setHasOnlyEmojis(ChatUtil.containsOnlyEmojis(message.getMessage()));
        response.setHasOnlyOneEmoji(ChatUtil.containsOnlyOneEmoji(message.getMessage()));
        response.setIsSentByCurrentUser(!message.getSender().equals(id));
        response.setReply(prepareRepliedResponse(message.getReplied(), id));
        response.setName(profileRepository.findById(message.getSender()).map(Profile::getFullName).orElse(""));
        response.setRoom(message.getChatRoom().getId());
        return response;
    }

    private ChatReplyResponse prepareRepliedResponse(ChatMessage message, UUID id) {
        if(message != null) {
            ChatReplyResponse response = ConversationMapper.INSTANCE.reply(message);
            response.setMessage(ChatUtil.formatRepliedMessage(message.getMessage(), message.getDuration(), message.getType()));
            response.setLabel(TimeUtil.formatTime(message.getCreatedAt(), profileRepository.findById(id).map(p -> p.getUser().getTimezone()).orElse("")));
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
    @Transactional
    public ChatMessage getLastMessage(List<ChatMessage> messages) {
        if(messages == null || messages.isEmpty()) {
            return null;
        } else {
            messages.removeIf(chatMessage -> chatMessage.getState() != MessageState.ACTIVE);
            if(messages.isEmpty()) {
                return null;
            }
            return Collections.max(messages, Comparator.comparing(ChatMessage::getCreatedAt));
        }
    }

    @Override
    @Transactional
    public List<ChatRoomResponse> response(UUID id) {
        List<ChatRoom> rooms = chatRoomRepository.findByUserId(id);
        if(rooms == null || rooms.isEmpty()) {
            return List.of();
        } else {
            return rooms.stream()
                    .filter(room -> room.getMessages() != null && !room.getMessages().isEmpty())
                    .filter(room -> !room.getMessages().stream().allMatch(msg -> msg.getState() == MessageState.DELETED))
                    .filter(room -> room.getUpdatedAt().toLocalDate().equals(LocalDate.now())
                            || bookmarkRepository.existsByUser_IdAndProvider_Id(room.getCreator(), room.getRoommate())
                            || bookmarkRepository.existsByUser_IdAndProvider_Id(room.getRoommate(), room.getCreator())
                    )
                    .map(this::response)
                    .sorted(Comparator.comparing(ChatRoomResponse::getSentAt))
                    .toList();
        }
    }

    @Override
    @Transactional
    public ChatRoomResponse response(ChatRoom room) {
        ChatRoomResponse response = new ChatRoomResponse();
        UUID id = isCurrentUser(room.getCreator()) ? room.getRoommate() : room.getCreator();

        prepareRoomResponse(id, response);
        response.setUser(userUtil.getUser().getId());
        response.setRoom(room.getId());

        ChatMessage message = getLastMessage(room.getMessages());
        if(message != null) {
            response.setLabel(TimeUtil.formatDay(message.getCreatedAt(), userUtil.getUser().getTimezone()));
            response.setStatus(message.getStatus());
            response.setMessage(ChatUtil.formatRoomMessage(message.getMessage(), message.getType(), isCurrentUser(message.getSender()), response.getName()));
            response.setSentAt(message.getCreatedAt());
        }
        response.setCount(chatMessageRepository.countMessagesReceivedByUser(room.getId(), userUtil.getUser().getId()));
        response.setGroups(response(room.getMessages()));

        return updateResponse(room, response, profileRepository.findById(id).map(profile -> profile.getUser().isProvider()).orElse(false));
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

                    chat.setLabel(TimeUtil.formatChatLabel(LocalDateTime.of(date, LocalTime.now()), userUtil.getUser().getTimezone()));
                    chat.setTime(ZonedDateTime.of(LocalDateTime.of(date, LocalTime.now()), TimeUtil.zoneId(userUtil.getUser().getTimezone())));

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
        response.setLabel(TimeUtil.formatTime(message.getCreatedAt(), userUtil.getUser().getTimezone()));
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
            response.setLabel(TimeUtil.formatTime(message.getCreatedAt(), userUtil.getUser().getTimezone()));
            response.setHasOnlyEmojis(ChatUtil.containsOnlyEmojis(message.getMessage()));
            response.setHasOnlyOneEmoji(ChatUtil.containsOnlyOneEmoji(message.getMessage()));
            response.setIsSentByCurrentUser(isCurrentUser(message.getSender()));
            response.setSender(profileRepository.findById(message.getSender()).map(Profile::getFullName).orElse(""));
            return response;
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public void clearChats() {
        chatMessageRepository.deleteAll(chatMessageRepository.findAllPastMessagesWithoutBookmarkedProvider());
    }
}
