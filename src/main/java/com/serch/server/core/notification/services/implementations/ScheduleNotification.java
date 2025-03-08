package com.serch.server.core.notification.services.implementations;

import com.serch.server.core.notification.repository.INotificationRepository;
import com.serch.server.core.notification.requests.NotificationMessage;
import com.serch.server.core.notification.requests.SerchNotification;
import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.domains.schedule.responses.ScheduleResponse;
import com.serch.server.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleNotification implements NotificationService {
    private final INotificationRepository notificationRepository;

    @Override
    public void send(UUID id, ScheduleResponse response) {
        log.info(String.format("Preparing schedule notification for %s, Category: %s to %s", response.getId(), response.getCategory(), id));
        response.setSnt("SCHEDULE");

        NotificationMessage<ScheduleResponse> message = new NotificationMessage<>();
        notificationRepository.getToken(String.valueOf(id)).ifPresent(token -> {
            message.setToken(token);
            message.setData(getScheduleNotification(response, false));
            message.setSnt(NotificationType.SCHEDULE);
            send(message);
        });

        notificationRepository.getBusinessToken(id).ifPresent(token -> {
            message.setToken(token);
            message.setData(getScheduleNotification(response, true));
            message.setSnt(NotificationType.SCHEDULE);
            send(message);
        });
    }

    private SerchNotification<ScheduleResponse> getScheduleNotification(ScheduleResponse response, boolean isBusiness) {
        if(isBusiness) {
            return switch (response.getStatus()) {
                case PENDING -> {
                    SerchNotification<ScheduleResponse> notification = new SerchNotification<>();
                    notification.setTitle("New schedule request");
                    notification.setBody(String.format("One of your providers have a new schedule request for %s. Tap to see details", response.getTime()));
                    notification.setData(response);

                    yield notification;
                }
                case ACCEPTED -> {
                    SerchNotification<ScheduleResponse> notification = new SerchNotification<>();
                    notification.setTitle("Active schedule");
                    notification.setBody(String.format("The schedule request for %s was accepted", response.getTime()));
                    notification.setData(response);

                    yield notification;
                }
                case DECLINED -> {
                    SerchNotification<ScheduleResponse> notification = new SerchNotification<>();
                    notification.setTitle("Declined schedule");
                    notification.setBody(String.format("The schedule request for %s was declined", response.getTime()));
                    notification.setData(response);

                    yield notification;
                }
                case CANCELLED -> {
                    SerchNotification<ScheduleResponse> notification = new SerchNotification<>();
                    notification.setTitle("Cancelled schedule");
                    notification.setBody(String.format("The schedule request for %s was cancelled. Notify your provider", response.getTime()));
                    notification.setData(response);

                    yield notification;
                }
                default -> {
                    SerchNotification<ScheduleResponse> notification = new SerchNotification<>();
                    notification.setTitle("Closed schedule");
                    notification.setBody(String.format("The schedule request for %s was closed", response.getTime()));
                    notification.setData(response);

                    yield notification;
                }
            };
        } else {
            return switch (response.getStatus()) {
                case PENDING -> {
                    SerchNotification<ScheduleResponse> notification = new SerchNotification<>();
                    notification.setTitle("New schedule request");
                    notification.setBody(String.format("You have a schedule request for %s from %s", response.getTime(), response.getName()));
                    notification.setData(response);

                    yield notification;
                }
                case ACCEPTED -> {
                    SerchNotification<ScheduleResponse> notification = new SerchNotification<>();
                    notification.setTitle("Active schedule");
                    notification.setBody(String.format("%s accepted your schedule request for %s", response.getName(), response.getTime()));
                    notification.setData(response);

                    yield notification;
                }
                case DECLINED -> {
                    SerchNotification<ScheduleResponse> notification = new SerchNotification<>();
                    notification.setTitle("Declined schedule");
                    notification.setBody(String.format("%s declined your schedule request for %s", response.getName(), response.getTime()));
                    notification.setData(response);

                    yield notification;
                }
                case CANCELLED -> {
                    SerchNotification<ScheduleResponse> notification = new SerchNotification<>();
                    notification.setTitle("Cancelled schedule");
                    notification.setBody(String.format("%s cancelled your schedule request for %s",response.getName(), response.getTime()));
                    notification.setData(response);

                    yield notification;
                }
                default -> {
                    SerchNotification<ScheduleResponse> notification = new SerchNotification<>();
                    notification.setTitle("Closed schedule");
                    notification.setBody(String.format("Your schedule request for %s was closed", response.getTime()));
                    notification.setData(response);

                    yield notification;
                }
            };
        }
    }
}