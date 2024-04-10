package com.serch.server.services.notification.core;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import com.serch.server.services.notification.requests.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationCore implements NotificationService {
    private final GoogleCredentials credentials;
    private FirebaseMessaging messaging() {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(options, "Serch");
        return FirebaseMessaging.getInstance(app);
    }
    private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis())
                .setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(
                        AndroidNotification.builder()
                                .setTag(topic)
                                .build()
                )
                .build();
    }
    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(
                        Aps.builder()
                                .setCategory(topic)
                                .setThreadId(topic)
                                .build()
                )
                .build();
    }
    private Message message(NotificationRequest request) {
        if(request.isTopic()) {
            AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
            ApnsConfig apnsConfig = getApnsConfig(request.getTopic());

            return Message.builder()
                    .setApnsConfig(apnsConfig)
                    .setAndroidConfig(androidConfig)
                    .putAllData(request.getData())
                    .setToken(request.getTopic())
                    .setNotification(
                            Notification.builder()
                                    .setBody(request.getMessage())
                                    .setImage(request.getImage())
                                    .setTitle(request.getTitle())
                                    .build()
                    )
                    .build();
        } else {
            AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
            ApnsConfig apnsConfig = getApnsConfig(request.getTopic());

            return Message.builder()
                    .setApnsConfig(apnsConfig)
                    .setAndroidConfig(androidConfig)
                    .putAllData(request.getData())
                    .setNotification(
                            Notification.builder()
                                    .setBody(request.getMessage())
                                    .setImage(request.getImage())
                                    .setTitle(request.getTitle())
                                    .build()
                    )
                    .build();
        }
    }

    @Override
    public void send(NotificationRequest request) {
        messaging().sendAsync(message(request));
    }

    @Override
    public void send(List<String> tokens, NotificationRequest request) {
        messaging().sendEachAsync(
                tokens.stream()
                        .map(s -> {
                            request.setToken(s);
                            return message(request);
                        })
                        .toList()
        );
    }

    @Override
    public void subscribe(String topic, List<String> tokens) {
        messaging().subscribeToTopicAsync(tokens, topic);
    }
}
