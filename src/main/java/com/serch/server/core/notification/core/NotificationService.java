package com.serch.server.core.notification.core;

import com.serch.server.domains.conversation.responses.ActiveCallResponse;
import com.serch.server.domains.conversation.responses.ChatRoomResponse;
import com.serch.server.domains.schedule.responses.ScheduleResponse;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Interface for sending notifications to users through various channels.
 * <p>
 * This interface defines methods for sending different types of notifications,
 * including chat messages, calls, schedules, trip updates, and transaction alerts.
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
    void send(UUID id, ChatRoomResponse response);

    /**
     * Sends a call notification to the preferred device of the specified user.
     *
     * @param id The UUID of the user receiving the notification.
     * @param response The {@link ActiveCallResponse} containing the call details.
     */
    void send(UUID id, ActiveCallResponse response);

    /**
     * Sends a schedule notification to the preferred device of the specified user.
     *
     * @param id The UUID of the user receiving the notification.
     * @param response The {@link ScheduleResponse} containing the schedule details.
     */
    void send(UUID id, ScheduleResponse response);

    /**
     * Sends a trip invite or request notification to the preferred device of the specified guest.
     *
     * @param id The ID of the guest receiving the notification.
     * @param content The content of the notification message.
     * @param title The title of the notification.
     * @param trip The ID of the trip (only applicable for trip requests, not invites).
     * @param sender The ID of the sender of the notification.
     * @param isInvite Indicates whether the notification is from a trip invite.
     */
    void send(String id, String content, String title, String sender, String trip, boolean isInvite);

    /**
     * Sends a transaction notification to the user for debit and credit transactions.
     *
     * @param id The UUID of the user receiving the notification.
     * @param isIncome Indicates whether the payment is classified as income.
     * @param amount The amount associated with the transaction.
     */
    void send(UUID id, boolean isIncome, BigDecimal amount);

    /**
     * Sends an uncleared transaction notification to the user for debit and credit transactions.
     *
     * @param id The UUID of the user receiving the notification.
     * @param amount The amount associated with the transaction.
     */
    void send(UUID id, BigDecimal amount);

    /**
     * Sends a transaction notification to the user for payouts.
     *
     * @param id The UUID of the user receiving the notification.
     * @param paid Indicates whether the payment is an income or a payout.
     * @param amount The amount associated with the payout.
     * @param next The date for the next payout.
     * @param bank The bank name and relevant details associated with the payout.
     */
    void send(UUID id, BigDecimal amount, boolean paid, String next, String bank);
}