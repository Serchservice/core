package com.serch.server.core.notification.services.implementations;

import com.serch.server.core.notification.repository.INotificationRepository;
import com.serch.server.core.notification.requests.NotificationMessage;
import com.serch.server.core.notification.requests.SerchNotification;
import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.domains.transaction.responses.TransactionResponse;
import com.serch.server.domains.transaction.services.TransactionResponseService;
import com.serch.server.enums.NotificationType;
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
public class TransactionNotification implements NotificationService {
    private final INotificationRepository notificationRepository;
    private final TransactionResponseService responseService;

    @Override
    public void send(UUID id, boolean isIncome, BigDecimal amount, String transaction) {
        log.info(String.format("Preparing transaction notification for %s to %s", amount, id));

        SerchNotification<Object> notification = new SerchNotification<>();
        notification.setTitle(isIncome
                ? String.format("Money In | %s! Keep increasing that wealth", MoneyUtil.formatToNaira(amount))
                : String.format("Money out | %s! You were debited", MoneyUtil.formatToNaira(amount)));
        notification.setBody(isIncome
                ? String.format("Your Serch wallet just received %s into your withdrawing balance", MoneyUtil.formatToNaira(amount))
                : String.format("%s was debited from your wallet. See details in your history.", MoneyUtil.formatToNaira(amount)));

        sendTransactionNotification(id, notification, transaction);
    }

    private void sendTransactionNotification(UUID id, SerchNotification<Object> notification, String transaction) {
        notification.setImage(notificationRepository.getAvatar(id.toString()));
        notification.setData(getTransactionData(id, transaction));

        notificationRepository.getToken(id.toString()).ifPresent(token -> {
            NotificationMessage<Object> message = new NotificationMessage<>();
            message.setToken(token);
            message.setData(notification);
            message.setSnt(NotificationType.TRANSACTION);
            send(message);
        });
    }

    private Object getTransactionData(UUID id, String transaction) {
        TransactionResponse response = responseService.response(transaction);
        if(response != null) {
            return response;
        }

        Map<String, String> data = new HashMap<>();
        data.put("sender_name", notificationRepository.getName(id.toString()));
        data.put("sender_id", String.valueOf(id));
        data.put("id", transaction);

        return data;
    }

    @Override
    public void send(UUID id, BigDecimal amount, String transaction) {
        log.info(String.format("Preparing uncleared transaction notification for %s to %s", amount, id));

        sendTransactionNotification(id, getUnclearedTransactionNotification(amount), transaction);
    }

    private SerchNotification<Object> getUnclearedTransactionNotification(BigDecimal amount) {
        SerchNotification<Object> notification = new SerchNotification<>();
        notification.setTitle(String.format("Money Out | %s! You were debited", MoneyUtil.formatToNaira(amount)));
        notification.setBody(String.format("%s was debited from your wallet which was used to clear your unpaid debts.", MoneyUtil.formatToNaira(amount)));

        return notification;
    }

    @Override
    public void send(UUID id, BigDecimal amount, boolean paid, String next, String bank, String transaction) {
        log.info(String.format("Preparing payout notification for %s to %s", amount, id));

        sendTransactionNotification(id, getPayoutNotification(amount, paid, next, bank), transaction);
    }

    private SerchNotification<Object> getPayoutNotification(BigDecimal amount, boolean paid, String next, String bank) {
        SerchNotification<Object> notification = new SerchNotification<>();
        notification.setTitle(paid ? "Yay!!! It's payday again. An exciting time in Serch" : "Seems like we won't be cashing out today.");
        notification.setBody(paid
                ? String.format("%s has successfully being cashed out to %s. Looking forward to the next payday - %s", MoneyUtil.formatToNaira(amount), bank, next)
                : "For some reasons, Serch couldn't process your payout today. You can check out our help center to see why this happened or reach out to customer support.");

        return notification;
    }
}