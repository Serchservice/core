package com.serch.server.domains.conversation.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.chat.MessageState;
import com.serch.server.exceptions.conversation.ChatException;
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
import com.serch.server.domains.conversation.responses.ChatGroupMessageResponse;
import com.serch.server.domains.conversation.responses.ChatMessageResponse;
import com.serch.server.domains.conversation.responses.ChatReplyResponse;
import com.serch.server.domains.conversation.responses.ChatRoomResponse;
import com.serch.server.domains.conversation.services.ChatService;
import com.serch.server.domains.schedule.services.SchedulingService;
import com.serch.server.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.serch.server.enums.account.SerchCategory.USER;

@Service
@RequiredArgsConstructor
public class ChatImplementation implements ChatService {
    private final AuthUtil authUtil;
    private final SchedulingService schedulingService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ScheduleRepository scheduleRepository;
    private final ActiveRepository activeRepository;

    @Override
    @Transactional
    public ApiResponse<List<ChatRoomResponse>> rooms(Integer page, Integer size) {
        List<ChatRoomResponse> list = getRoomList(authUtil.getUser().getId(), page, size);

        return new ApiResponse<>(list);
    }

    @Transactional
    protected List<ChatRoomResponse> getRoomList(UUID id, Integer page, Integer size) {
        Page<ChatRoom> rooms = chatRoomRepository.findFilteredChatRooms(id, HelperUtil.getPageable(page, size));
        List<ChatRoomResponse> list = new ArrayList<>();

        if(rooms != null && rooms.hasContent() && !rooms.isEmpty()) {
            list = rooms.getContent()
                    .stream()
                    .map(room -> getChatRoomResponse(room, id))
                    .sorted(Comparator.comparing(ChatRoomResponse::getSentAt))
                    .toList();
        }

        return list;
    }

    @Override
    @Transactional
    public List<ChatRoomResponse> rooms(UUID id) {
        return getRoomList(id, null, null);
    }

    @Override
    @Transactional
    public ChatRoomResponse getChatRoomResponse(ChatRoom room, UUID user) {
        List<ChatMessage> messages = chatMessageRepository.findActiveMessages(room.getId());

        ChatRoomResponse response = new ChatRoomResponse();
        response.setRoom(room.getId());
        response.setTotal(messages.size());

        prepareRoomResponse(room.getCreator().equals(user) ? room.getRoommate() : room.getCreator(), response);

        ChatMessage message = getLastMessage(messages);
        if(message != null) {
            response.setLabel(TimeUtil.formatDay(message.getCreatedAt(), userRepository.findById(user).map(User::getTimezone).orElse("")));
            response.setStatus(message.getStatus());
            response.setMessage(ChatUtil.formatRoomMessage(getMessage(message, user), message.getType(), message.getSender().equals(user), response.getName()));
            response.setSentAt(message.getCreatedAt());
            response.setType(message.getType());
        }

        response.setCount(chatMessageRepository.countMessagesReceivedByUser(room.getId(), user));
        response.setGroups(getGroupMessageList(room.getId(), null, null, user));

        return updateResponse(room, response, profileRepository.findById(user).map(profile -> profile.getUser().isProvider()).orElse(false));
    }

    private String getMessage(ChatMessage message, UUID id) {
        return message.getSender().equals(id) && !message.getSenderMessage().isEmpty() ? message.getSenderMessage() : message.getMessage();
    }

    @Transactional
    protected void prepareRoomResponse(UUID roommate, ChatRoomResponse response) {
        Profile profile = profileRepository.findById(roommate).orElse(null);

        if (profile != null) {
            response.setAvatar(profile.getAvatar());
            response.setCategory(profile.getCategory().getType());
            response.setImage(profile.getCategory().getImage());
            response.setLastSeen(TimeUtil.formatLastSignedIn(profile.getUser().getLastSignedIn(), profile.getUser().getTimezone(), false));
            response.setName(profile.getFullName());
            response.setPublicKey(DatabaseUtil.decodeData(profile.getPublicEncryptionKey()));
        } else {
            response.setAvatar("");
            response.setCategory(USER.getType());
            response.setImage(USER.getImage());
            response.setLastSeen(TimeUtil.formatLastSignedIn(TimeUtil.now(), "", false));
            response.setName("");
            response.setPublicKey("");
        }

        response.setRoommate(roommate);
        response.setIsActive(activeRepository.findByProfile_Id(roommate).map(Active::isActive).orElse(false));
        response.setTrip(activeRepository.findByProfile_Id(roommate).map(active -> active.getStatus().getType()).orElse(""));
    }

    @Transactional
    protected ChatMessage getLastMessage(List<ChatMessage> messages) {
        if(messages == null || messages.isEmpty()) {
            return null;
        } else {
            return Collections.max(messages, Comparator.comparing(ChatMessage::getCreatedAt));
        }
    }

    @Transactional
    protected List<ChatGroupMessageResponse> getGroupMessageList(String room, Integer page, Integer size, UUID id) {
        Page<ChatMessage> messages = chatMessageRepository.findActiveMessages(room, HelperUtil.getPageable(page, size));

        if(messages == null || messages.isEmpty()) {
            return new ArrayList<>();
        } else {
            if(messages.isEmpty()) {
                return new ArrayList<>();
            } else {
                Map<LocalDate, List<ChatMessage>> messagesByDate = messages.getContent()
                        .stream()
                        .collect(Collectors.groupingBy(message -> message.getCreatedAt().toLocalDate()));

                List<ChatGroupMessageResponse> response = new ArrayList<>();
                messagesByDate.forEach((date, chats) -> {
                    ChatGroupMessageResponse chat = new ChatGroupMessageResponse();

                    String timezone = userRepository.findById(id).map(User::getTimezone).orElse("");
                    chat.setLabel(TimeUtil.formatChatLabel(LocalDateTime.of(date, chats.getFirst().getCreatedAt().toLocalTime()), timezone));
                    chat.setTime(ZonedDateTime.of(LocalDateTime.of(date, chats.getFirst().getCreatedAt().toLocalTime()), TimeUtil.zoneId(timezone)));

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

        response.setMessage(getMessage(message, id));
        response.setLabel(TimeUtil.formatTime(message.getCreatedAt(), profileRepository.findById(id).map(p -> p.getUser().getTimezone()).orElse("")));
        response.setIsSentByCurrentUser(message.getSender().equals(id));
        response.setReply(prepareRepliedResponse(message.getReplied(), id));
        response.setName(profileRepository.findById(message.getSender()).map(Profile::getFullName).orElse(""));
        response.setRoom(message.getChatRoom().getId());

        return response;
    }

    private ChatReplyResponse prepareRepliedResponse(ChatMessage message, UUID id) {
        if(message != null) {
            ChatReplyResponse response = ConversationMapper.INSTANCE.reply(message);

            response.setMessage(ChatUtil.formatRepliedMessage(getMessage(message, id), message.getDuration(), message.getType()));
            response.setLabel(TimeUtil.formatTime(message.getCreatedAt(), profileRepository.findById(id).map(p -> p.getUser().getTimezone()).orElse("")));
            response.setIsSentByCurrentUser(message.getSender().equals(id));
            response.setSender(profileRepository.findById(message.getSender()).map(Profile::getFullName).orElse(""));

            return response;
        } else {
            return null;
        }
    }

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

    @Override
    @Transactional
    public ApiResponse<List<ChatGroupMessageResponse>> messages(String roomId, Integer page, Integer size) {
        return new ApiResponse<>(getGroupMessageList(roomId, page, size, authUtil.getUser().getId()));
    }

    @Override
    @Transactional
    public ApiResponse<ChatRoomResponse> room(String roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException("Chat not found", String.valueOf(authUtil.getUser().getId())));

        return new ApiResponse<>(getChatRoomResponse(room, authUtil.getUser().getId()));
    }

    @Override
    @Transactional
    public ApiResponse<ChatRoomResponse> getOrCreate(UUID roommate) {
        ChatRoom room = chatRoomRepository.findByRoommateAndCreator(roommate, authUtil.getUser().getId())
                .orElseGet(() -> {
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setCreator(authUtil.getUser().getId());
                    newRoom.setRoommate(roommate);
                    newRoom.setState(MessageState.ACTIVE);
                    return chatRoomRepository.save(newRoom);
                });

        return new ApiResponse<>(getChatRoomResponse(room, authUtil.getUser().getId()));
    }
}