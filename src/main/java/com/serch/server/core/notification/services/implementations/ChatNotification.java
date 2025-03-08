package com.serch.server.core.notification.services.implementations;

import com.serch.server.core.notification.repository.INotificationRepository;
import com.serch.server.core.notification.requests.NotificationMessage;
import com.serch.server.core.notification.requests.SerchNotification;
import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.domains.conversation.responses.ChatRoomResponse;
import com.serch.server.enums.NotificationType;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatNotification implements NotificationService {
    private final INotificationRepository notificationRepository;

    @Override
    public void send(UUID receiver, ChatRoomResponse response) {
        log.info(String.format("Preparing chat notification for %s to %s", response.getRoom(), receiver));

        notificationRepository.getToken(String.valueOf(receiver)).ifPresent(token -> {
            NotificationMessage<Object> message = new NotificationMessage<>();
            message.setToken(token);
            message.setData(getChatNotification(response));
            message.setSnt(NotificationType.CHAT);
            send(message);
        });
    }

    private SerchNotification<Object> getChatNotification(ChatRoomResponse response) {
        SerchNotification<Object> notification = new SerchNotification<>();

        if(response.getCategory().equalsIgnoreCase("user")) {
            notification.setTitle(response.getName());
        } else {
            notification.setTitle(String.format("%s (%s)", response.getName(), HelperUtil.textWithAorAn(response.getCategory())));
        }
        notification.setBody(response.getMessage());
        notification.setData(getChatData(response));

        return notification;
    }

    private Object getChatData(ChatRoomResponse response) {
        String summary = response.getCount() > 1
                ? "%s messages".formatted(response.getCount())
                : "%s message".formatted(response.getCount());
        summary = String.format("%s from %s", summary, response.getRoom().substring(0, 6).replaceAll("-", ""));

        response.setSummary(summary);

        return getChatData(response, summary);
    }

    private Map<String, String> getChatData(ChatRoomResponse response, String summary) {
        Map<String, String> data = new HashMap<>();
        data.put("room", response.getRoom());
        data.put("id", response.getMessageId());
        data.put("roommate", response.getRoommate().toString());
        data.put("image", response.getAvatar());
        data.put("category", response.getCategory());
        data.put("name", response.getName());
        data.put("e_pub_key", response.getPublicKey());
        data.put("summary", summary);

        return data;
    }
}