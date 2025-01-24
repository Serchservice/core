package com.serch.server.core.notification.services.implementations;

import com.serch.server.core.notification.services.NotificationCoreService;
import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.core.notification.requests.SerchNotification;
import com.serch.server.domains.conversation.responses.ActiveCallResponse;
import com.serch.server.domains.conversation.responses.ChatRoomResponse;
import com.serch.server.core.notification.repository.INotificationRepository;
import com.serch.server.core.notification.requests.NotificationMessage;
import com.serch.server.domains.schedule.responses.ScheduleResponse;
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

        repository.getToken(String.valueOf(receiver)).ifPresent(token -> {
            NotificationMessage<Map<String, String>> message = new NotificationMessage<>();
            message.setToken(token);
            message.setData(getChatNotification(response));
            message.setSnt("CHAT");
            notificationCoreService.send(message);
        });
    }

    private SerchNotification<Map<String, String>> getChatNotification(ChatRoomResponse response) {
        SerchNotification<Map<String, String>> notification = new SerchNotification<>();

        if(response.getCategory().equalsIgnoreCase("user")) {
            notification.setTitle("New message");
        } else {
            notification.setTitle(String.format("New message from %s", HelperUtil.textWithAorAn(response.getCategory())));
        }
        notification.setBody(response.getMessage());
        notification.setData(getChatData(response));

        return notification;
    }

    private Map<String, String> getChatData(ChatRoomResponse response) {
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
        data.put("name", response.getName());
        data.put("e_pub_key", response.getPublicKey());
        data.put("summary", String.format("%s from %s", summary, shortRoomName));
        data.put("snt", "CHAT");

        return data;
    }

    @Override
    public void send(UUID id, ActiveCallResponse response) {
        response.setSnt("CALL");

        repository.getToken(String.valueOf(id)).ifPresent(token -> {
            NotificationMessage<ActiveCallResponse> message = new NotificationMessage<>();
            message.setToken(token);
            message.setData(getCallNotification(response));
            message.setSnt("CALL");
            notificationCoreService.send(message);
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

    @Override
    public void send(UUID id, ScheduleResponse response) {
        log.info(String.format("Preparing schedule notification for %s, Category: %s to %s", response.getId(), response.getCategory(), id));
        response.setSnt("SCHEDULE");

        NotificationMessage<ScheduleResponse> message = new NotificationMessage<>();
        repository.getToken(String.valueOf(id)).ifPresent(token -> {
            message.setToken(token);
            message.setData(getScheduleNotification(response, false));
            message.setSnt("SCHEDULE");
            notificationCoreService.send(message);
        });

        repository.getBusinessToken(id).ifPresent(token -> {
            message.setToken(token);
            message.setData(getScheduleNotification(response, true));
            message.setSnt("SCHEDULE");
            notificationCoreService.send(message);
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

    @Override
    public void send(String id, String content, String title, String sender, String trip, boolean isInvite) {
        log.info(String.format("Preparing trip notification for %s from %s to %s", trip, sender, id));

        repository.getToken(id).ifPresent(token -> {
            NotificationMessage<Map<String, Object>> message = new NotificationMessage<>();
            message.setToken(token);
            message.setData(getTripNotification(content, title, sender, getTripData(sender, trip, isInvite)));
            message.setSnt("TRIP_MESSAGE");
            notificationCoreService.send(message);
        });
    }

    private Map<String, Object> getTripData(String sender, String trip, boolean isInvite) {
        Map<String, Object> data = new HashMap<>();
        data.put("sender_name", repository.getName(sender));
        data.put("sender_id", sender);
        data.put("can_act", trip != null);
        data.put("is_request", isInvite);
        data.put("type", isInvite ? "REQUEST" : "");

        if(trip != null) {
            data.put("trip_id", trip);
        }

        return data;
    }

    private SerchNotification<Map<String, Object>> getTripNotification(String content, String title, String sender, Map<String, Object> data) {
        SerchNotification<Map<String, Object>> notification = new SerchNotification<>();
        notification.setTitle(title);
        notification.setBody(content);
        notification.setImage(repository.getAvatar(sender));
        notification.setData(data);

        return notification;
    }

    @Override
    public void send(UUID id, boolean isIncome, BigDecimal amount, String transaction) {
        log.info(String.format("Preparing transaction notification for %s to %s", amount, id));

        SerchNotification<Map<String, String>> notification = new SerchNotification<>();
        notification.setTitle(isIncome
                ? String.format("Money In | %s! Keep increasing that wealth", MoneyUtil.formatToNaira(amount))
                : String.format("Money out | %s! You were debited", MoneyUtil.formatToNaira(amount)));
        notification.setBody(isIncome
                ? String.format("Your Serch wallet just received %s into your withdrawing balance", MoneyUtil.formatToNaira(amount))
                : String.format("%s was debited from your wallet. See details in your history.", MoneyUtil.formatToNaira(amount)));

        sendTransactionNotification(id, notification, transaction);
    }

    private void sendTransactionNotification(UUID id, SerchNotification<Map<String, String>> notification, String transaction) {
        notification.setImage(repository.getAvatar(id.toString()));
        notification.setData(getTransactionData(id, transaction));

        repository.getToken(id.toString()).ifPresent(token -> {
            NotificationMessage<Map<String, String>> message = new NotificationMessage<>();
            message.setToken(token);
            message.setData(notification);
            message.setSnt("TRANSACTION");
            notificationCoreService.send(message);
        });
    }

    private Map<String, String> getTransactionData(UUID id, String transaction) {
        Map<String, String> data = new HashMap<>();
        data.put("sender_name", repository.getName(id.toString()));
        data.put("sender_id", String.valueOf(id));
        data.put("id", transaction);

        return data;
    }

    @Override
    public void send(UUID id, BigDecimal amount, String transaction) {
        log.info(String.format("Preparing uncleared transaction notification for %s to %s", amount, id));

        sendTransactionNotification(id, getUnclearedTransactionNotification(amount), transaction);
    }

    private SerchNotification<Map<String, String>> getUnclearedTransactionNotification(BigDecimal amount) {
        SerchNotification<Map<String, String>> notification = new SerchNotification<>();
        notification.setTitle(String.format("Money Out | %s! You were debited", MoneyUtil.formatToNaira(amount)));
        notification.setBody(String.format("%s was debited from your wallet which was used to clear your unpaid debts.", MoneyUtil.formatToNaira(amount)));

        return notification;
    }

    @Override
    public void send(UUID id, BigDecimal amount, boolean paid, String next, String bank, String transaction) {
        log.info(String.format("Preparing payout notification for %s to %s", amount, id));

        sendTransactionNotification(id, getPayoutNotification(amount, paid, next, bank), transaction);
    }

    private SerchNotification<Map<String, String>> getPayoutNotification(BigDecimal amount, boolean paid, String next, String bank) {
        SerchNotification<Map<String, String>> notification = new SerchNotification<>();
        notification.setTitle(paid ? "Yay!!! It's payday again. An exciting time in Serch" : "Seems like we won't be cashing out today.");
        notification.setBody(paid
                ? String.format("%s has successfully being cashed out to %s. Looking forward to the next payday - %s", MoneyUtil.formatToNaira(amount), bank, next)
                : "For some reasons, Serch couldn't process your payout today. You can check out our help center to see why this happened or reach out to customer support.");

        return notification;
    }
}
