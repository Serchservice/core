package com.serch.server.core.notification.services.implementations;

import com.serch.server.core.notification.repository.INotificationRepository;
import com.serch.server.core.notification.requests.NotificationMessage;
import com.serch.server.core.notification.requests.SerchNotification;
import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.enums.NotificationType;
import com.serch.server.domains.nearby.services.bcap.responses.GoBCapLifecycleResponse;
import com.serch.server.domains.nearby.services.activity.responses.GoActivityLifecycleResponse;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestTrendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoNotification implements NotificationService {
    private final INotificationRepository goNotificationRepository;

    @Override
    public void send(GoActivityLifecycleResponse response, UUID id) {
        goNotificationRepository.getToken(String.valueOf(id)).ifPresent(token -> {
            NotificationMessage<GoActivityLifecycleResponse> message = new NotificationMessage<>();
            message.setToken(token);
            message.setData(getActivity(response));
            message.setSnt(NotificationType.GO_ACTIVITY);
            send(message);
        });
    }

    private SerchNotification<GoActivityLifecycleResponse> getActivity(GoActivityLifecycleResponse response) {
        SerchNotification<GoActivityLifecycleResponse> notification = new SerchNotification<>();
        notification.setTitle(response.getTitle());
        notification.setBody(response.getMessage());
        notification.setData(response);

        return notification;
    }

    @Override
    public void send(GoBCapLifecycleResponse response, UUID id) {
        goNotificationRepository.getToken(String.valueOf(id)).ifPresent(token -> {
            NotificationMessage<GoBCapLifecycleResponse> message = new NotificationMessage<>();
            message.setToken(token);
            message.setData(getActivity(response));
            message.setSnt(NotificationType.GO_BCAP);
            send(message);
        });
    }

    private SerchNotification<GoBCapLifecycleResponse> getActivity(GoBCapLifecycleResponse response) {
        SerchNotification<GoBCapLifecycleResponse> notification = new SerchNotification<>();
        notification.setTitle(response.getTitle());
        notification.setBody(response.getMessage());
        notification.setData(response);

        return notification;
    }

    @Override
    public void send(GoInterestTrendResponse response, UUID id) {
        goNotificationRepository.getToken(String.valueOf(id)).ifPresent(token -> {
            NotificationMessage<GoInterestTrendResponse> message = new NotificationMessage<>();
            message.setToken(token);
            message.setData(getActivity(response));
            message.setSnt(NotificationType.GO_TREND);
            send(message);
        });
    }

    private SerchNotification<GoInterestTrendResponse> getActivity(GoInterestTrendResponse response) {
        SerchNotification<GoInterestTrendResponse> notification = new SerchNotification<>();
        notification.setTitle(response.getTitle());
        notification.setBody(response.getMessage());
        notification.setData(response);

        return notification;
    }
}