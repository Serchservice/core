package com.serch.server.core.notification.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.serch.server.core.notification.requests.NotificationMessage;
import com.serch.server.core.notification.requests.SerchNotification;
import com.serch.server.core.notification.services.NotificationCoreService;
import com.serch.server.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCore implements NotificationCoreService {
    private final ObjectMapper objectMapper;

    @Override
    public void send(NotificationMessage<?> request) {
        Message message = Message.builder()
                .setToken(request.getToken())
                .putAllData(toMap(request.getData(), request.getSnt()))
                .build();
        log.info(String.format("%s::: %s", "NOTIFICATION SDK REQUEST", request.getToken()));

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info(String.format("%s::: %s", "NOTIFICATION SDK RESPONSE", response));
        } catch (FirebaseMessagingException e) {
            log.error(String.format("%s::: %s", "NOTIFICATION SDK EXCEPTION", e));
        }
    }

    @SneakyThrows
    private Map<String, String> toMap(SerchNotification<?> data, NotificationType snt) {
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

            map.put("data", objectMapper.writeValueAsString(data.getData()));
        }

        return map;
    }
}