package com.serch.server.core.notification.services.implementations;

import com.serch.server.core.notification.repository.INotificationRepository;
import com.serch.server.core.notification.requests.NotificationMessage;
import com.serch.server.core.notification.requests.SerchNotification;
import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripNotification implements NotificationService {
    private final INotificationRepository notificationRepository;

    @Override
    public void send(String id, String content, String title, String sender, String trip, boolean isInvite) {
        log.info(String.format("Preparing trip notification for %s from %s to %s", trip, sender, id));

        notificationRepository.getToken(id).ifPresent(token -> {
            NotificationMessage<Map<String, Object>> message = new NotificationMessage<>();
            message.setToken(token);
            message.setData(getTripNotification(content, title, sender, getTripData(sender, trip, isInvite)));
            message.setSnt(NotificationType.TRIP_MESSAGE);
            send(message);
        });
    }

    private Map<String, Object> getTripData(String sender, String trip, boolean isInvite) {
        Map<String, Object> data = new HashMap<>();
        data.put("sender_name", notificationRepository.getName(sender));
        data.put("sender_id", sender);
        data.put("can_act", trip != null);
        data.put("is_request", isInvite);
        data.put("type", isInvite ? "REQUEST" : "");

        if(trip != null) {
            data.put("trip_id", trip);
        } else {
            data.put("trip", UUID.randomUUID().toString());
        }

        return data;
    }

    private SerchNotification<Map<String, Object>> getTripNotification(String content, String title, String sender, Map<String, Object> data) {
        SerchNotification<Map<String, Object>> notification = new SerchNotification<>();
        notification.setTitle(title);
        notification.setBody(content);
        notification.setImage(notificationRepository.getAvatar(sender));
        notification.setData(data);

        return notification;
    }
}
