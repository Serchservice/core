package com.serch.server.domains.conversation.services.implementations;

import com.serch.server.core.notification.core.NotificationService;
import com.serch.server.enums.chat.MessageState;
import com.serch.server.enums.chat.MessageStatus;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.conversation.ChatMessage;
import com.serch.server.models.conversation.ChatRoom;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.conversation.ChatMessageRepository;
import com.serch.server.repositories.conversation.ChatRoomRepository;
import com.serch.server.domains.conversation.requests.MessageTypingRequest;
import com.serch.server.domains.conversation.requests.SendMessageRequest;
import com.serch.server.domains.conversation.requests.UpdateMessageRequest;
import com.serch.server.domains.conversation.responses.ChatRoomResponse;
import com.serch.server.domains.conversation.services.ChatService;
import com.serch.server.domains.conversation.services.ChattingService;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChattingImplementation implements ChattingService {
    private final SimpMessagingTemplate template;
    private final ChatService chatService;
    private final NotificationService notificationService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final UserUtil userUtil;

    private boolean isCurrentUser(UUID id, UUID current) {
        return id.equals(current);
    }

    @Override
    @Transactional
    public void send(SendMessageRequest request, String emailAddress) {
        userRepository.findByEmailAddressIgnoreCase(emailAddress).ifPresent(user -> chatRoomRepository.findById(request.getRoom()).ifPresent(room -> {
            ChatMessage message = new ChatMessage();

            message.setMessage(request.getMessage());
            message.setSenderMessage(request.getSenderMessage());
            message.setType(request.getType());

            if(request.getReplied() != null && !request.getReplied().isEmpty()) {
                ChatMessage replied = chatMessageRepository.findById(request.getReplied()).orElse(null);
                message.setReplied(replied);
            }

            message.setChatRoom(room);
            message.setSender(user.getId());
            chatMessageRepository.save(message);
            chatMessageRepository.flush();

            room.setUpdatedAt(TimeUtil.now());
            chatRoomRepository.save(room);

            sendMessage(room, user, true);
        }));
    }

    @Override
    @Transactional
    public void refresh(String roomId, String emailAddress) {
        userRepository.findByEmailAddressIgnoreCase(emailAddress)
                .ifPresent(user -> chatRoomRepository.findById(roomId)
                        .ifPresent(room -> sendMessage(room, user, false)));
    }

    @Override
    @Transactional
    public void markAllAsRead(String roomId, String emailAddress) {
        userRepository.findByEmailAddressIgnoreCase(emailAddress).ifPresent(user -> {
            List<ChatMessage> messages = chatMessageRepository.findUnreadMessages(roomId, user.getId());
            if (messages != null && !messages.isEmpty()) {
                messages.forEach(message -> {
                    message.setStatus(MessageStatus.READ);
                    message.setUpdatedAt(TimeUtil.now());
                    chatMessageRepository.save(message);
                    chatMessageRepository.flush();
                });

                sendMessage(messages.getFirst().getChatRoom(), user, false);
            }
        });
    }

    @Override
    @Transactional
    public void notifyTyping(MessageTypingRequest request, String emailAddress) {
        userRepository.findByEmailAddressIgnoreCase(emailAddress).ifPresent(user -> chatRoomRepository.findById(request.getRoom()).ifPresent(room -> {
            UUID id = isCurrentUser(room.getCreator(), user.getId()) ? room.getRoommate() : room.getCreator();
            template.convertAndSend("/platform/%s/chat/%s/typing".formatted(String.valueOf(id), room.getId()), request.getState());
        }));

    }

    @Override
    @Transactional
    public void update(UpdateMessageRequest request, String emailAddress) {
        userRepository.findByEmailAddressIgnoreCase(emailAddress).ifPresent(user -> {
            ChatMessage message = chatMessageRepository.findById(request.getId()).orElse(null);
            ChatRoom room = chatRoomRepository.findById(request.getRoom()).orElse(null);

            if(message != null && room != null) {
                if(request.getState() != null && isCurrentUser(message.getSender(), user.getId())) {
                    message.setState(MessageState.DELETED);
                    message.setUpdatedAt(TimeUtil.now());
                    chatMessageRepository.save(message);

                    sendMessage(room, user, false);
                } else if(!isCurrentUser(message.getSender(), user.getId())) {
                    message.setStatus(request.getStatus());
                    message.setUpdatedAt(TimeUtil.now());
                    chatMessageRepository.save(message);

                    sendMessage(room, user, false);
                }
            }
        });
    }

    @Override
    @Transactional
    public void updateAll(UpdateMessageRequest request, String emailAddress) {
        userRepository.findByEmailAddressIgnoreCase(emailAddress).ifPresent(user -> chatRoomRepository.findById(request.getRoom()).ifPresent(room -> {
            if(request.getState() != null) {
                room.getMessages().stream()
                        .filter(message -> isCurrentUser(message.getSender(), user.getId()))
                        .forEach(message -> {
                            message.setState(MessageState.DELETED);
                            message.setUpdatedAt(TimeUtil.now());
                            chatMessageRepository.save(message);
                        });

                sendMessage(room, user, false);
            } else {
                room.getMessages().stream()
                        .filter(message -> !isCurrentUser(message.getSender(), user.getId()))
                        .forEach(message -> {
                            message.setState(request.getState());
                            message.setUpdatedAt(TimeUtil.now());
                            chatMessageRepository.save(message);
                        });

                sendMessage(room, user, false);
            }
        }));
    }

    @Transactional
    protected void sendMessage(ChatRoom room, User user, boolean notify) {
        sendToSender(room, user);
        sendToReceiver(room, user, notify);
    }

    @Transactional
    protected void sendToSender(ChatRoom room, User user) {
        ChatRoomResponse response = chatService.getChatRoomResponse(room, user.getId());

        template.convertAndSend("/platform/%s/chat/%s".formatted(String.valueOf(user.getId()), room.getId()), response);
        template.convertAndSend("/platform/%s/chat".formatted(String.valueOf(user.getId())), chatService.rooms(user.getId()));
    }

    @Transactional
    protected void sendToReceiver(ChatRoom room, User user, boolean notify) {
        UUID id = isCurrentUser(room.getCreator(), user.getId()) ? room.getRoommate() : room.getCreator();
        ChatRoomResponse response =  chatService.getChatRoomResponse(room, id);

        template.convertAndSend("/platform/%s/chat/%s".formatted(String.valueOf(id), room.getId()), response);
        template.convertAndSend("/platform/%s/chat".formatted(String.valueOf(id)), chatService.rooms(id));

        if(notify) {
            notificationService.send(id, response);
        }
    }

    @Override
    @Transactional
    public void announce(String room, String emailAddress) {
        profileRepository.findByUser_EmailAddress(emailAddress)
                .ifPresent(profile -> chatRoomRepository.findById(room).ifPresentOrElse(
                        chatRoom -> announcePresence(chatRoom, profile),
                        () -> chatRoomRepository.findFilteredChatRooms(profile.getId()).forEach(channel -> announcePresence(channel, profile))
                )
        );
    }

    @Transactional
    protected void announcePresence(ChatRoom room, Profile profile) {
        List<ChatMessage> messages = chatMessageRepository.findMessagesReceivedByUser(room.getId(), profile.getId());

        if(messages != null && !messages.isEmpty()) {
            messages.forEach(message -> {
                message.setStatus(MessageStatus.DELIVERED);
                message.setUpdatedAt(TimeUtil.now());
                chatMessageRepository.save(message);
            });
        }

        UUID id = isCurrentUser(room.getCreator(), profile.getId()) ? room.getRoommate() : room.getCreator();
        template.convertAndSend("/platform/%s/chat/presence".formatted(id), "%s is now online".formatted(profile.getUser().getFullName()));

        sendMessage(room, profile.getUser(), false);
    }

    @Override
    @Transactional
    public void notifyAboutSchedule(UUID roommate) {
        profileRepository.findById(userUtil.getUser().getId())
                .flatMap(profile -> chatRoomRepository.findRoom(roommate, profile.getId()))
                .ifPresent(room -> sendMessage(room, userUtil.getUser(), false));
    }

    @Override
    @Transactional
    public void clearChats() {
        chatMessageRepository.deleteAll(chatMessageRepository.findAllPastMessagesWithoutBookmarkedProvider());
    }
}
