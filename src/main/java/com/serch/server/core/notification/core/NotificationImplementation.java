package com.serch.server.core.notification.core;

import com.serch.server.services.conversation.responses.ActiveCallResponse;
import com.serch.server.services.conversation.responses.ChatRoomResponse;
import com.serch.server.core.notification.repository.INotificationRepository;
import com.serch.server.core.notification.requests.Notification;
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
        message.setNotification(Notification.builder()
                .title(String.format("New message from %s", HelperUtil.textWithAorAn(response.getCategory())))
                .body(String.format("%s sent you a message", response.getName()))
                .build());

        Map<String, Object> data = getChatNotification(response);
        message.setData(data);
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
        NotificationMessage<ActiveCallResponse> message = new NotificationMessage<>();
        message.setToken(repository.getToken(String.valueOf(id)));
        message.setNotification(Notification.builder()
                .title(String.format("Incoming %s call", response.getType().getType()))
                .body(String.format("From %s (%s)", response.getName(), response.getCategory()))
                .image(response.getAvatar())
                .build());
        response.setSnt("CALL");
        message.setData(response);
        notificationCoreService.send(message);
    }

    @Override
    public void send(UUID id, ScheduleResponse response) {
        NotificationMessage<ScheduleResponse> message = new NotificationMessage<>();
        message.setToken(repository.getToken(String.valueOf(id)));
        message.setNotification(getScheduleNotification(response, false));
        response.setSnt("SCHEDULE");
        message.setData(response);
        notificationCoreService.send(message);

        if(!repository.getBusinessToken(id).isEmpty()) {
            message.setToken(repository.getBusinessToken(id));
            message.setNotification(getScheduleNotification(response, true));
            response.setSnt("SCHEDULE");
            message.setData(response);
            notificationCoreService.send(message);
        }
    }

    private Notification getScheduleNotification(ScheduleResponse response, boolean isBusiness) {
        if(isBusiness) {
            return switch (response.getStatus()) {
                case PENDING -> Notification.builder()
                        .title("New schedule request")
                        .body(String.format("One of your providers have a new schedule request for %s. Tap to see details", response.getTime()))
                        .build();
                case ACCEPTED -> Notification.builder()
                        .title("Active schedule")
                        .body(String.format("The schedule request for %s was accepted", response.getTime()))
                        .build();
                case DECLINED -> Notification.builder()
                        .title("Declined schedule")
                        .body(String.format("The schedule request for %s was declined", response.getTime()))
                        .build();
                case CANCELLED -> Notification.builder()
                        .title("Cancelled schedule")
                        .body(String.format("The schedule request for %s was cancelled. Notify your provider", response.getTime()))
                        .build();
                default -> Notification.builder()
                        .title("Closed schedule")
                        .body(String.format("The schedule request for %s was closed", response.getTime()))
                        .build();
            };
        } else {
            return switch (response.getStatus()) {
                case PENDING -> Notification.builder()
                        .title("New schedule request")
                        .body(String.format("You have a schedule request for %s from %s", response.getTime(), response.getName()))
                        .build();
                case ACCEPTED -> Notification.builder()
                        .title("Active schedule")
                        .body(String.format("%s accepted your schedule request for %s", response.getName(), response.getTime()))
                        .build();
                case DECLINED -> Notification.builder()
                        .title("Declined schedule")
                        .body(String.format("%s declined your schedule request for %s", response.getName(), response.getTime()))
                        .build();
                case CANCELLED -> Notification.builder()
                        .title("Cancelled schedule")
                        .body(String.format("%s cancelled your schedule request for %s",response.getName(), response.getTime()))
                        .build();
                default -> Notification.builder()
                        .title("Closed schedule")
                        .body(String.format("Your schedule request for %s was closed", response.getTime()))
                        .build();
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
        message.setNotification(Notification.builder()
                .title(title)
                .body(content)
                .image(repository.getAvatar(sender))
                .build());

        Map<String, Object> data = new HashMap<>();
        data.put("snt", "TRIP_MESSAGE");
        data.put("sender_name", repository.getName(sender));
        data.put("sender_id", sender);
        data.put("can_act", trip != null);
        data.put("is_request", isInvite);

        if(trip != null) {
            data.put("trip_id", trip);
        }
        message.setData(data);
        notificationCoreService.send(message);
    }

    @Override
    public void send(UUID id, boolean isIncome, BigDecimal amount) {
        NotificationMessage<Map<String, Object>> notification = new NotificationMessage<>();
        notification.setToken(repository.getToken(id.toString()));
        notification.setNotification(Notification.builder()
                .title(isIncome
                        ? String.format("Money In - %s! Keep increasing that wealth", MoneyUtil.formatToNaira(amount))
                        : String.format("Money out - %s! You were debited", MoneyUtil.formatToNaira(amount))
                )
                .body(isIncome
                        ? String.format("Your Serch wallet just received %s into your withdrawing balance", MoneyUtil.formatToNaira(amount))
                        : String.format("%s was debited from your wallet for a trip service charge", MoneyUtil.formatToNaira(amount)))
                .image(repository.getAvatar(id.toString()))
                .build());

        Map<String, Object> data = new HashMap<>();
        data.put("snt", "TRANSACTION");
        data.put("sender_name", repository.getName(id.toString()));
        data.put("sender_id", id);

        notification.setData(data);
        notificationCoreService.send(notification);
    }
}
