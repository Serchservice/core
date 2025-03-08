package com.serch.server.core.notification.services;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.serch.server.core.notification.requests.NotificationMessage;
import com.serch.server.core.notification.requests.SerchNotification;
import com.serch.server.domains.conversation.responses.ActiveCallResponse;
import com.serch.server.domains.conversation.responses.ChatRoomResponse;
import com.serch.server.domains.schedule.responses.ScheduleResponse;
import com.serch.server.enums.NotificationType;
import com.serch.server.domains.nearby.services.bcap.responses.GoBCapLifecycleResponse;
import com.serch.server.domains.nearby.services.activity.responses.GoActivityLifecycleResponse;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestTrendResponse;
import lombok.SneakyThrows;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Interface for sending notifications to users through various channels.
 * <p>
 * This interface defines methods for sending different types of notifications.
 * Implementations of this interface should handle the underlying notification delivery
 * mechanisms to ensure users receive timely and relevant information on their preferred devices.
 * </p>
 */
public interface NotificationService {
    /**
     * Sends a chat notification to the preferred device of the specified user.
     *
     * @param id The UUID of the user receiving the notification.
     * @param response The {@link ChatRoomResponse} containing the chat message details.
     */
    default void send(UUID id, ChatRoomResponse response) { }

    /**
     * Sends a call notification to the preferred device of the specified user.
     *
     * @param id The UUID of the user receiving the notification.
     * @param response The {@link ActiveCallResponse} containing the call details.
     */
    default void send(UUID id, ActiveCallResponse response) { }

    /**
     * Sends a schedule notification to the preferred device of the specified user.
     *
     * @param id The UUID of the user receiving the notification.
     * @param response The {@link ScheduleResponse} containing the schedule details.
     */
    default void send(UUID id, ScheduleResponse response) { }

    /**
     * Sends a trip invite or request notification to the preferred device of the specified guest.
     *
     * @param id The ID of the guest receiving the notification.
     * @param content The content of the notification message.
     * @param title The title of the notification.
     * @param trip The ID of the trip (only applicable for trip dtos, not invites).
     * @param sender The ID of the sender of the notification.
     * @param isInvite Indicates whether the notification is from a trip invite.
     */
    default void send(String id, String content, String title, String sender, String trip, boolean isInvite) { }

    /**
     * Sends a transaction notification to the user for debit and credit transactions.
     *
     * @param transaction The transaction id
     * @param id The UUID of the user receiving the notification.
     * @param isIncome Indicates whether the payment is classified as income.
     * @param amount The amount associated with the transaction.
     */
    default void send(UUID id, boolean isIncome, BigDecimal amount, String transaction) { }

    /**
     * Sends an uncleared transaction notification to the user for debit and credit transactions.
     *
     * @param transaction The transaction id
     * @param id The UUID of the user receiving the notification.
     * @param amount The amount associated with the transaction.
     */
    default void send(UUID id, BigDecimal amount, String transaction) { }

    /**
     * Sends a transaction notification to the user for payouts.
     *
     * @param transaction The transaction id
     * @param id The UUID of the user receiving the notification.
     * @param paid Indicates whether the payment is an income or a payout.
     * @param amount The amount associated with the payout.
     * @param next The date for the next payout.
     * @param bank The bank name and relevant details associated with the payout.
     */
    default void send(UUID id, BigDecimal amount, boolean paid, String next, String bank, String transaction) { }

    /**
     * Sends a notification based on the GoEvent lifecycle.
     *
     * @param response The {@link GoActivityLifecycleResponse} containing the activity lifecycle details.
     * @param id The UUID of the user receiving the notification.
     */
    default void send(GoActivityLifecycleResponse response, UUID id) { }

    /**
     * Sends a notification based on the GoBCap lifecycle.
     *
     * @param response The {@link GoBCapLifecycleResponse} containing the BCap lifecycle details.
     * @param id The UUID of the user receiving the notification.
     */
    default void send(GoBCapLifecycleResponse response, UUID id) { }

    /**
     * Sends a notification based on the GoInterest trend.
     *
     * @param response The {@link GoInterestTrendResponse} containing the interest trend details.
     * @param id The UUID of the user receiving the notification.
     */
    default void send(GoInterestTrendResponse response, UUID id) { }

    /**
     * Sends a notification to a specific device.
     * <p>
     * This method accepts a notification message request, which
     * contains all the necessary information to compose and send
     * the notification. The implementation should ensure that the
     * notification is delivered to the intended device or user.
     * </p>
     *
     * @param request The {@link NotificationMessage} request that contains
     *                the details of the notification to be sent, including
     *                the recipient, message content, and any relevant metadata.
     *                The message can be of a generic type to allow for
     *                flexibility in the content structure.
     */
    default void send(NotificationMessage<?> request) {
        Message message = Message.builder()
                .setToken(request.getToken())
                .putAllData(toMap(request.getData(), request.getSnt()))
                .build();
        System.out.printf("%s::: %s", "NOTIFICATION SDK REQUEST", request.getToken());

        send(message);
    }

    @SneakyThrows
    private Map<String, String> toMap(SerchNotification<?> data, NotificationType snt) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.modulesToInstall(new JavaTimeModule());
        builder.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Map<String, String> map = new HashMap<>();

        // Convert response field if it's not null
        if (data != null) {
            // Manually convert important fields
            map.put("title", data.getTitle());
            map.put("body", data.getBody());
            map.put("snt", snt.name());
            if (data.getImage() != null) {
                map.put("image", data.getImage());
            }

            map.put("data", builder.build().writeValueAsString(data.getData()));
        }

        return map;
    }

    private void send(Message message) {
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.printf("%s::: %s", "NOTIFICATION SDK RESPONSE", response);
        } catch (FirebaseMessagingException e) {
            System.err.printf("%s::: %s", "NOTIFICATION SDK EXCEPTION", e);
        }
    }

    default void send(String token, String title, String body) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .build();
        System.out.printf("%s::: %s", "NOTIFICATION SDK REQUEST", token);

        send(message);
    }
}