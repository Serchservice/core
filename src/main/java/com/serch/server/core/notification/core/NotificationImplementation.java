package com.serch.server.core.notification.core;

import com.serch.server.core.notification.requests.SerchNotification;
import com.serch.server.services.conversation.responses.ActiveCallResponse;
import com.serch.server.services.conversation.responses.ChatRoomResponse;
import com.serch.server.core.notification.repository.INotificationRepository;
import com.serch.server.core.notification.requests.NotificationMessage;
import com.serch.server.services.schedule.responses.ScheduleResponse;
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
        log.info(String.format("Preparing chat notification for %s to %s", response.getRoom(), receiver));

        SerchNotification<Map<String, String>> notification = new SerchNotification<>();

        if(response.getCategory().equalsIgnoreCase("user")) {
            notification.setTitle("New message");
        } else {
            notification.setTitle(String.format("New message from %s", HelperUtil.textWithAorAn(response.getCategory())));
        }
        notification.setBody(String.format("%s sent you a message", response.getName()));
        notification.setData(getChatNotification(response));

        NotificationMessage<Map<String, String>> message = new NotificationMessage<>();
        message.setToken(repository.getToken(String.valueOf(receiver)));
        message.setData(notification);
        message.setSnt("CHAT");
        notificationCoreService.send(message);
    }

    private static Map<String, String> getChatNotification(ChatRoomResponse response) {
        String summary = response.getCount() > 1
                ? "%s messages".formatted(response.getCount())
                : "%s message".formatted(response.getCount());
        String shortRoomName = response.getRoom().substring(0, 6).replaceAll("-", "");

        Map<String, String> data = new HashMap<>();
        data.put("room", response.getRoom());
        data.put("id", response.getMessageId());
        data.put("roommate", response.getRoommate().toString());
        data.put("image", response.getAvatar());
        data.put("category", response.getCategory());
        data.put("summary", String.format("%s from %s", summary, shortRoomName));
        data.put("snt", "CHAT");
        return data;
    }

    @Override
    public void send(UUID id, ActiveCallResponse response) {
        response.setSnt("CALL");

        SerchNotification<ActiveCallResponse> notification = new SerchNotification<>();
        notification.setTitle(String.format("Incoming %s call", response.getType().getType()));
        notification.setBody(String.format("From %s (%s)", response.getName(), response.getCategory()));
        notification.setBody(response.getAvatar());
        notification.setData(response);

        NotificationMessage<ActiveCallResponse> message = new NotificationMessage<>();
        message.setToken(repository.getToken(String.valueOf(id)));
        message.setData(notification);
        message.setSnt("CALL");
        notificationCoreService.send(message);
    }

    @Override
    public void send(UUID id, ScheduleResponse response) {
        log.info(String.format("Preparing schedule notification for %s, Category: %s to %s", response.getId(), response.getCategory(), id));
        response.setSnt("SCHEDULE");

        NotificationMessage<ScheduleResponse> message = new NotificationMessage<>();
        message.setToken(repository.getToken(String.valueOf(id)));
        message.setData(getScheduleNotification(response, false));
        message.setSnt("SCHEDULE");
        notificationCoreService.send(message);

        if(!repository.getBusinessToken(id).isEmpty()) {
            message.setToken(repository.getBusinessToken(id));
            message.setData(getScheduleNotification(response, true));
            message.setSnt("SCHEDULE");
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
    public void send(String id, String content, String title, String sender, String trip, boolean isInvite) {
        log.info(String.format("Preparing trip notification for %s from %s to %s", trip, sender, id));

        Map<String, String> data = new HashMap<>();
        data.put("snt", "TRIP_MESSAGE");
        data.put("sender_name", repository.getName(sender));
        data.put("sender_id", sender);
        data.put("can_act", String.valueOf(trip != null));
        data.put("is_request", String.valueOf(isInvite));

        if(trip != null) {
            data.put("trip_id", trip);
        }

        SerchNotification<Map<String, String>> notification = new SerchNotification<>();
        notification.setTitle(title);
        notification.setBody(content);
        notification.setImage(repository.getAvatar(sender));
        notification.setData(data);

        NotificationMessage<Map<String, String>> message = new NotificationMessage<>();
        message.setToken(repository.getToken(id));
        message.setData(notification);
        message.setSnt("TRIP_MESSAGE");
        notificationCoreService.send(message);
    }

    @Override
    public void send(UUID id, boolean isIncome, BigDecimal amount) {
        log.info(String.format("Preparing transaction notification for %s to %s", amount, id));

        SerchNotification<Map<String, String>> notification = new SerchNotification<>();
        notification.setTitle(isIncome
                ? String.format("Money In | %s! Keep increasing that wealth", MoneyUtil.formatToNaira(amount))
                : String.format("Money out | %s! You were debited", MoneyUtil.formatToNaira(amount)));
        notification.setBody(isIncome
                ? String.format("Your Serch wallet just received %s into your withdrawing balance", MoneyUtil.formatToNaira(amount))
                : String.format("%s was debited from your wallet. See details in your history.", MoneyUtil.formatToNaira(amount)));
        sendTransactionNotification(id, notification);
    }

    private void sendTransactionNotification(UUID id, SerchNotification<Map<String, String>> notification) {
        notification.setImage(repository.getAvatar(id.toString()));
        notification.setData(getTransactionData(id));

        NotificationMessage<Map<String, String>> message = new NotificationMessage<>();
        message.setToken(repository.getToken(id.toString()));
        message.setData(notification);
        message.setSnt("TRANSACTION");
        notificationCoreService.send(message);
    }

    private Map<String, String> getTransactionData(UUID id) {
        Map<String, String> data = new HashMap<>();
        data.put("snt", "TRANSACTION");
        data.put("sender_name", repository.getName(id.toString()));
        data.put("sender_id", String.valueOf(id));
        return data;
    }

    @Override
    public void send(UUID id, BigDecimal amount) {
        log.info(String.format("Preparing uncleared transaction notification for %s to %s", amount, id));

        SerchNotification<Map<String, String>> notification = new SerchNotification<>();
        notification.setTitle(String.format("Money Out | %s! You were debited", MoneyUtil.formatToNaira(amount)));
        notification.setBody(String.format("%s was debited from your wallet which was used to clear your unpaid debts.", MoneyUtil.formatToNaira(amount)));
        sendTransactionNotification(id, notification);
    }

    @Override
    public void send(UUID id, BigDecimal amount, boolean paid, String next, String bank) {
        log.info(String.format("Preparing payout notification for %s to %s", amount, id));

        SerchNotification<Map<String, String>> notification = new SerchNotification<>();
        notification.setTitle(paid ? "Yay!!! It's payday again. An exciting time in Serch" : "Seems like we won't be cashing out today.");
        notification.setBody(paid
                ? String.format("%s has successfully being cashed out to %s. Looking forward to the next payday - %s", MoneyUtil.formatToNaira(amount), bank, next)
                : "For some reasons, Serch couldn't process your payout today. You can check out our help center to see why this happened or reach out to customer support.");
        sendTransactionNotification(id, notification);
    }
}
