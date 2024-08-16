package com.serch.server.core.notification.core;

import com.serch.server.services.conversation.responses.ActiveCallResponse;
import com.serch.server.services.conversation.responses.ChatRoomResponse;
import com.serch.server.services.schedule.responses.ScheduleResponse;
import com.serch.server.services.trip.responses.TripResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface NotificationService {
    /**
     * This will send a chat notification to the preferred device
     *
     * @param id The user receiving the notification
     * @param response The {@link ChatRoomResponse} converted data
     */
    void send(UUID id, ChatRoomResponse response);

    /**
     * This will send a call notification to the preferred device
     *
     * @param id The user receiving the notification
     * @param response The {@link ActiveCallResponse} converted data
     */
    void send(UUID id, ActiveCallResponse response);

    /**
     * This will send a schedule notification to the preferred device
     *
     * @param id The user receiving the notification
     * @param response The {@link ScheduleResponse} converted data
     */
    void send(UUID id, ScheduleResponse response);

    /**
     * This will send a trip notification to the preferred device
     *
     * @param id The guest receiving the notification
     * @param response The {@link TripResponse} converted data
     */
    void send(String id, TripResponse response);

    /**
     * This will send a trip invite or request notification to the preferred device
     *
     * @param id The guest receiving the notification
     * @param content The content of the notification
     * @param title The title of the notification
     * @param trip The id of the trip (Only for trip requests not trip invites)
     * @param sender The sender id
     * @param isInvite Whether the notification is from trip invite
     */
    void send(String id, String content, String title, String sender, String trip, boolean isInvite);

    /**
     * This will send a trip invite or request notification to the preferred device
     *
     * @param id The guest receiving the notification
     * @param isIncome Whether the payment is an income or not
     * @param amount The amount from the payment
     */
    void send(UUID id, boolean isIncome, BigDecimal amount);
}