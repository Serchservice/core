package com.serch.server.services.notification.core;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Value("${application.notification.private.key}")
    private String NOTIFICATION_PRIVATE_KEY;
    @Value("${application.notification.private.key.id}")
    private String NOTIFICATION_PRIVATE_KEY_ID;
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

    @Bean
    @SneakyThrows
    GoogleCredentials credentials() {
        Map<String, Object> values = new HashMap<>();
        values.put("type", NOTIFICATION_TYPE);
        values.put("project_id", NOTIFICATION_PROJECT_ID);
        values.put("private_key_id", NOTIFICATION_PRIVATE_KEY_ID);
        values.put("private_key", NOTIFICATION_PRIVATE_KEY);
        values.put("client_email", NOTIFICATION_CLIENT_EMAIL);
        values.put("client_id", NOTIFICATION_CLIENT_ID);
        values.put("auth_uri", NOTIFICATION_AUTH_URI);
        values.put("token_uri", NOTIFICATION_TOKEN_URI);
        values.put("auth_provider_x509_cert_url", NOTIFICATION_AUTH_PROVIDER_X509_CERT_URL);
        values.put("client_x509_cert_url", NOTIFICATION_CLIENT_X509_CERT_URL);
        values.put("universe_domain", NOTIFICATION_UNIVERSE_DOMAIN);

        ObjectMapper mapper = new ObjectMapper();
        JsonMapper map = new JsonMapper();
        String serviceAccount = map.writeValueAsString(values);
        return GoogleCredentials.fromStream(new ByteArrayInputStream(serviceAccount.getBytes()));
    }
}
