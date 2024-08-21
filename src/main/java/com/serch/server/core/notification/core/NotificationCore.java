package com.serch.server.core.notification.core;

import com.google.auth.oauth2.GoogleCredentials;
import com.serch.server.core.notification.requests.FCM;
import com.serch.server.core.notification.requests.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCore implements NotificationCoreService {
    private final RestTemplate template;
    private final GoogleCredentials credentials;

    @Value("${application.notification.base-url}")
    private String NOTIFICATION_BASE_URL;

    @SneakyThrows
    private HttpHeaders headers() {
        credentials.refreshIfExpired();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer "+ credentials.getAccessToken().getTokenValue());
        return headers;
    }

    @Override
    public void send(NotificationMessage<?> request) {
        CompletableFuture.runAsync(() -> {
            FCM fcm = new FCM();
            fcm.setMessage(request);
            log.info(String.valueOf(fcm), "NOTIFICATION CORE REQUEST");

            HttpEntity<FCM> entity = new HttpEntity<>(fcm, headers());
            ResponseEntity<Object> response = template.exchange(NOTIFICATION_BASE_URL, HttpMethod.POST, entity, Object.class);
            log.info(String.valueOf(response), "NOTIFICATION CORE RESPONSE");
            log.info(String.valueOf(response.getStatusCode()), "NOTIFICATION CORE RESPONSE STATUS CODE");
            log.info(String.valueOf(response.getBody()), "NOTIFICATION CORE RESPONSE BODY");
        });
    }
}
