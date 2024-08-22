package com.serch.server.core.notification.core;

import com.serch.server.core.notification.requests.SerchNotification;
import com.serch.server.services.conversation.responses.ActiveCallResponse;
import com.serch.server.services.conversation.responses.ChatRoomResponse;
import com.serch.server.core.notification.repository.INotificationRepository;
import com.serch.server.core.notification.requests.NotificationMessage;
import com.serch.server.services.schedule.responses.ScheduleResponse;
import com.serch.server.services.trip.responses.TripResponse;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationImplementation implements NotificationService {
    private final NotificationCoreService notificationCoreService;
    private final INotificationRepository repository;

    @Override
    public void send(UUID receiver, ChatRoomResponse response) {
        NotificationMessage<Map<String, Object>> message = new NotificationMessage<>();
        message.setToken(repository.getToken(String.valueOf(receiver)));

        SerchNotification<Map<String, Object>> notification = new SerchNotification<>();
        notification.setTitle(String.format("New message from %s", HelperUtil.textWithAorAn(response.getCategory())));
        notification.setBody(String.format("%s sent you a message", response.getName()));
        notification.setData(getChatNotification(response));

        message.setData(notification);
        notificationCoreService.send(message);
    }

    private static Map<String, Object> getChatNotification(ChatRoomResponse response) {
        String summary = response.getCount() > 1
                ? "%s messages".formatted(response.getCount())
                : "%s message".formatted(response.getCount());
        String shortRoomName = response.getRoom().substring(0, 6).replaceAll("-", "");

        Map<String, Object> data = new HashMap<>();
        data.put("room", response.getRoom());
        data.put("id", response.getMessageId());
        data.put("roommate", response.getRoommate());
        data.put("image", response.getAvatar());
        data.put("category", response.getCategory());
        data.put("summary", String.format("%s from %s", summary, shortRoomName));
        data.put("snt", "CHAT");
        return data;
    }

    @Override
    public void send(UUID id, ActiveCallResponse response) {
        response.setSnt("CALL");

        NotificationMessage<ActiveCallResponse> message = new NotificationMessage<>();
        message.setToken(repository.getToken(String.valueOf(id)));

        SerchNotification<ActiveCallResponse> notification = new SerchNotification<>();
        notification.setTitle(String.format("Incoming %s call", response.getType().getType()));
        notification.setBody(String.format("From %s (%s)", response.getName(), response.getCategory()));
        notification.setBody(response.getAvatar());
        notification.setData(response);

        message.setData(notification);
        notificationCoreService.send(message);
    }

    @Override
    public void send(UUID id, ScheduleResponse response) {
        response.setSnt("SCHEDULE");

        NotificationMessage<ScheduleResponse> message = new NotificationMessage<>();
        message.setToken(repository.getToken(String.valueOf(id)));
        message.setData(getScheduleNotification(response, false));
        notificationCoreService.send(message);

        if(!repository.getBusinessToken(id).isEmpty()) {
            message.setToken(repository.getBusinessToken(id));
            message.setData(getScheduleNotification(response, true));
            notificationCoreService.send(message);
        }
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

    @Override
    public void send(String id, TripResponse response) {

    }

    @Override
    public void send(String id, String content, String title, String sender, String trip, boolean isInvite) {
        NotificationMessage<Map<String, Object>> message = new NotificationMessage<>();
        message.setToken(repository.getToken(id));

        Map<String, Object> data = new HashMap<>();
        data.put("snt", "TRIP_MESSAGE");
        data.put("sender_name", repository.getName(sender));
        data.put("sender_id", sender);
        data.put("can_act", trip != null);
        data.put("is_request", isInvite);

        if(trip != null) {
            data.put("trip_id", trip);
        }

        SerchNotification<Map<String, Object>> notification = new SerchNotification<>();
        notification.setTitle(title);
        notification.setBody(content);
        notification.setImage(repository.getAvatar(sender));
        notification.setData(data);

        message.setData(notification);
        notificationCoreService.send(message);
    }

    @Override
    public void send(UUID id, boolean isIncome, BigDecimal amount) {
        NotificationMessage<Map<String, Object>> message = new NotificationMessage<>();
        message.setToken(repository.getToken(id.toString()));

        Map<String, Object> data = new HashMap<>();
        data.put("snt", "TRANSACTION");
        data.put("sender_name", repository.getName(id.toString()));
        data.put("sender_id", id);

        SerchNotification<Map<String, Object>> notification = new SerchNotification<>();
        notification.setTitle(isIncome
                ? String.format("Money In - %s! Keep increasing that wealth", MoneyUtil.formatToNaira(amount))
                : String.format("Money out - %s! You were debited", MoneyUtil.formatToNaira(amount)));
        notification.setBody(isIncome
                ? String.format("Your Serch wallet just received %s into your withdrawing balance", MoneyUtil.formatToNaira(amount))
                : String.format("%s was debited from your wallet for a trip service charge", MoneyUtil.formatToNaira(amount)));
        notification.setImage(repository.getAvatar(id.toString()));
        notification.setData(data);

        message.setData(notification);
        notificationCoreService.send(message);
    }
}
