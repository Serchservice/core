package com.serch.server.services.notification.core;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class NotificationCredential {
    @Value("${application.notification.type}")
    private String NOTIFICATION_TYPE;

    @Value("${application.notification.project-id}")
    private String NOTIFICATION_PROJECT_ID;

    @Value("${application.notification.client.email}")
    private String NOTIFICATION_CLIENT_EMAIL;

    @Value("${application.notification.client.id}")
    private String NOTIFICATION_CLIENT_ID;

    @Value("${application.notification.uri.auth}")
    private String NOTIFICATION_AUTH_URI;

    @Value("${application.notification.uri.token}")
    private String NOTIFICATION_TOKEN_URI;

    @Value("${application.notification.x509.cert.url.auth}")
    private String NOTIFICATION_AUTH_PROVIDER_X509_CERT_URL;

    @Value("${application.notification.x509.cert.url.client}")
    private String NOTIFICATION_CLIENT_X509_CERT_URL;

    @Value("${application.notification.universe-domain}")
    private String NOTIFICATION_UNIVERSE_DOMAIN;

    @Value("${application.notification.private.key.id}")
    private String NOTIFICATION_PRIVATE_KEY_ID;

    @Value("${application.notification.private.key.p1}")
    private String NOTIFICATION_PRIVATE_KEY_P1;

    @Value("${application.notification.private.key.p2}")
    private String NOTIFICATION_PRIVATE_KEY_P2;

    @Value("${application.notification.private.key.p3}")
    private String NOTIFICATION_PRIVATE_KEY_P3;

    @Value("${application.notification.private.key.p4}")
    private String NOTIFICATION_PRIVATE_KEY_P4;

    @Value("${application.notification.private.key.p5}")
    private String NOTIFICATION_PRIVATE_KEY_P5;

    @Value("${application.notification.private.key.p6}")
    private String NOTIFICATION_PRIVATE_KEY_P6;

    @Value("${application.notification.private.key.p7}")
    private String NOTIFICATION_PRIVATE_KEY_P7;

    @Value("${application.notification.private.key.p8}")
    private String NOTIFICATION_PRIVATE_KEY_P8;

    @Value("${application.notification.private.key.p9}")
    private String NOTIFICATION_PRIVATE_KEY_P9;

    @Value("${application.notification.private.key.p10}")
    private String NOTIFICATION_PRIVATE_KEY_P10;

    @Value("${application.notification.private.key.p11}")
    private String NOTIFICATION_PRIVATE_KEY_P11;

    @Value("${application.notification.private.key.p12}")
    private String NOTIFICATION_PRIVATE_KEY_P12;

    @Value("${application.notification.private.key.p13}")
    private String NOTIFICATION_PRIVATE_KEY_P13;

    @Value("${application.notification.private.key.p14}")
    private String NOTIFICATION_PRIVATE_KEY_P14;

    @Value("${application.notification.private.key.p15}")
    private String NOTIFICATION_PRIVATE_KEY_P15;

    @Value("${application.notification.private.key.p16}")
    private String NOTIFICATION_PRIVATE_KEY_P16;

    @Value("${application.notification.private.key.p17}")
    private String NOTIFICATION_PRIVATE_KEY_P17;

    @Value("${application.notification.private.key.p18}")
    private String NOTIFICATION_PRIVATE_KEY_P18;

    @Value("${application.notification.private.key.p19}")
    private String NOTIFICATION_PRIVATE_KEY_P19;

    @Value("${application.notification.private.key.p20}")
    private String NOTIFICATION_PRIVATE_KEY_P20;

    @Value("${application.notification.private.key.p21}")
    private String NOTIFICATION_PRIVATE_KEY_P21;

    @Value("${application.notification.private.key.p22}")
    private String NOTIFICATION_PRIVATE_KEY_P22;

    @Value("${application.notification.private.key.p23}")
    private String NOTIFICATION_PRIVATE_KEY_P23;

    @Value("${application.notification.private.key.p24}")
    private String NOTIFICATION_PRIVATE_KEY_P24;

    @Value("${application.notification.private.key.p25}")
    private String NOTIFICATION_PRIVATE_KEY_P25;

    @Value("${application.notification.private.key.p26}")
    private String NOTIFICATION_PRIVATE_KEY_P26;

    @Value("${application.notification.private.key.p27}")
    private String NOTIFICATION_PRIVATE_KEY_P27;

    @Value("${application.notification.private.key.p28}")
    private String NOTIFICATION_PRIVATE_KEY_P28;


    private String buildPrivateKey() {
        return String.join(
                "\n",
                NOTIFICATION_PRIVATE_KEY_P1, NOTIFICATION_PRIVATE_KEY_P2,
                NOTIFICATION_PRIVATE_KEY_P3, NOTIFICATION_PRIVATE_KEY_P4,
                NOTIFICATION_PRIVATE_KEY_P5, NOTIFICATION_PRIVATE_KEY_P6,
                NOTIFICATION_PRIVATE_KEY_P7, NOTIFICATION_PRIVATE_KEY_P8,
                NOTIFICATION_PRIVATE_KEY_P9, NOTIFICATION_PRIVATE_KEY_P10,
                NOTIFICATION_PRIVATE_KEY_P11, NOTIFICATION_PRIVATE_KEY_P12,
                NOTIFICATION_PRIVATE_KEY_P13, NOTIFICATION_PRIVATE_KEY_P14,
                NOTIFICATION_PRIVATE_KEY_P15, NOTIFICATION_PRIVATE_KEY_P16,
                NOTIFICATION_PRIVATE_KEY_P17, NOTIFICATION_PRIVATE_KEY_P18,
                NOTIFICATION_PRIVATE_KEY_P19, NOTIFICATION_PRIVATE_KEY_P20,
                NOTIFICATION_PRIVATE_KEY_P21, NOTIFICATION_PRIVATE_KEY_P22,
                NOTIFICATION_PRIVATE_KEY_P23, NOTIFICATION_PRIVATE_KEY_P24,
                NOTIFICATION_PRIVATE_KEY_P25, NOTIFICATION_PRIVATE_KEY_P26,
                NOTIFICATION_PRIVATE_KEY_P27, NOTIFICATION_PRIVATE_KEY_P28
        );
    }

    @Bean
    @SneakyThrows
    GoogleCredentials credentials() {
        Map<String, Object> values = new HashMap<>();
        values.put("type", NOTIFICATION_TYPE);
        values.put("project_id", NOTIFICATION_PROJECT_ID);
        values.put("private_key_id", NOTIFICATION_PRIVATE_KEY_ID);
        values.put("private_key", buildPrivateKey());
        values.put("client_email", NOTIFICATION_CLIENT_EMAIL);
        values.put("client_id", NOTIFICATION_CLIENT_ID);
        values.put("auth_uri", NOTIFICATION_AUTH_URI);
        values.put("token_uri", NOTIFICATION_TOKEN_URI);
        values.put("auth_provider_x509_cert_url", NOTIFICATION_AUTH_PROVIDER_X509_CERT_URL);
        values.put("client_x509_cert_url", NOTIFICATION_CLIENT_X509_CERT_URL);
        values.put("universe_domain", NOTIFICATION_UNIVERSE_DOMAIN);

        JsonMapper map = new JsonMapper();
        String serviceAccount = map.writeValueAsString(values);
        return GoogleCredentials.fromStream(new ByteArrayInputStream(serviceAccount.getBytes()));
    }
}