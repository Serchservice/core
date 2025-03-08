package com.serch.server.core.notification.services.implementations;

import com.serch.server.core.notification.repository.INotificationRepository;
import com.serch.server.core.notification.requests.NotificationMessage;
import com.serch.server.core.notification.requests.SerchNotification;
import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.domains.conversation.responses.ActiveCallResponse;
import com.serch.server.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallNotification implements NotificationService {
    private final INotificationRepository notificationRepository;

    @Override
    public void send(UUID id, ActiveCallResponse response) {
        response.setSnt("CALL");

        notificationRepository.getToken(String.valueOf(id)).ifPresent(token -> {
            NotificationMessage<ActiveCallResponse> message = new NotificationMessage<>();
            message.setToken(token);
            message.setData(getCallNotification(response));
            message.setSnt(NotificationType.CALL);
            send(message);
        });
    }

    private SerchNotification<ActiveCallResponse> getCallNotification(ActiveCallResponse response) {
        SerchNotification<ActiveCallResponse> notification = new SerchNotification<>();
        notification.setTitle(String.format("Incoming %s call", response.getType().getType()));
        notification.setBody(String.format("From %s (%s)", response.getName(), response.getCategory()));
        notification.setBody(response.getAvatar());
        notification.setData(response);

        return notification;
    }
}