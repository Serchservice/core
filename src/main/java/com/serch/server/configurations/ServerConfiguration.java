package com.serch.server.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.mailersend.sdk.MailerSend;
import com.serch.server.core.NotificationKey;
import com.serch.server.core.sms.SmsConfig;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.utils.ServerUtil;
import com.twilio.Twilio;
import io.getstream.chat.java.services.framework.DefaultClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * The ServerConfiguration class configured various beans and settings related to server operations.
 * It is annotated with @Configuration to indicate that it defines application beans.
 * <p></p>
 * Additionally, it uses constructor injection for dependency management.
 * <p>
 * Use this to convert json to string for proper secure fetch
 * {@code cat /path/to/your/serviceAccountKey.json | jq -c .}
 *
 * @see org.springframework.context.annotation.Configuration
 */
@Configuration
@RequiredArgsConstructor
public class ServerConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ServerConfiguration.class);
    private final UserRepository userRepository;

    @Value("${spring.mail.password}")
    private String MAIL_API_KEY;

    @Value("${application.notification.key.service}")
    private String NOTIFICATION_SERVICE_KEY;

    @Value("${application.call.api-key}")
    private String CALL_APP_ID;

    @Value("${application.call.api-secret}")
    private String CALL_APP_SECRET;

    @Value("${application.sms.secret}")
    private String SMS_SECRET;

    @Value("${application.sms.auth_token}")
    private String SMS_AUTH_TOKEN;

    @Value("${application.sms.phone_number}")
    private String SMS_PHONE_NUMBER;

    /**
     * Configures a RestTemplate bean for making RESTful HTTP requests.
     *
     * @return A RestTemplate instance.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Configures an AuthenticationManager bean for managing authentication.
     *
     * @param authenticationConfiguration The authentication configuration.
     * @return An AuthenticationManager instance.
     * @throws Exception If an error occurs while retrieving the authentication manager.
     */
    @Bean
    public AuthenticationManager authManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures a UserDetailsService bean for retrieving user details during authentication.
     *
     * @return A UserDetailsService instance.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmailAddressIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Configures an AuthenticationProvider bean for authentication.
     *
     * @return An AuthenticationProvider instance.
     */
    @Bean
    public AuthenticationProvider authProvider() {
        var daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService());
        daoProvider.setPasswordEncoder(passwordEncoder());
        return daoProvider;
    }

    /**
     * Configures a PasswordEncoder bean for encoding passwords.
     *
     * @return A PasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures a CorsConfigurationSource bean for configuring CORS settings.
     *
     * @return A CorsConfigurationSource instance.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ServerUtil.PRODUCTION);
        configuration.setAllowedOriginPatterns(ServerUtil.DEVELOPMENT);
        configuration.setAllowedMethods(ServerUtil.METHODS);
        configuration.setAllowedHeaders(ServerUtil.HEADERS);
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public ObjectMapper objectMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.modulesToInstall(new JavaTimeModule());
        builder.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return builder.build();
    }

    @Bean
    @SneakyThrows
    public GoogleCredentials credentials() {
        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        if(NOTIFICATION_SERVICE_KEY.startsWith("https")) {
            NotificationKey key = restTemplate().getForObject(NOTIFICATION_SERVICE_KEY, NotificationKey.class);
            // Convert the map back to a JSON string
            json = objectMapper.writeValueAsString(key);
        } else {
            // Parse the JSON string into a Map
            var account = objectMapper.readValue(NOTIFICATION_SERVICE_KEY, HashMap.class);

            // Convert the map back to a JSON string
            json = objectMapper.writeValueAsString(account);
        }

        InputStream inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));

        // Refresh credentials if expired
        credentials.refreshIfExpired();

        return credentials;
    }

    @Bean
    public FirebaseApp firebase() {
        FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials()).build();
        FirebaseApp app = FirebaseApp.initializeApp(options);
        log.info(String.format("SERCH::: Firebase Initialized with app - %s", app.getName()));

        return app;
    }

    @Bean
    public DefaultClient defaultClient() {
        var properties = new Properties();
        properties.put(DefaultClient.API_KEY_PROP_NAME, CALL_APP_ID);
        properties.put(DefaultClient.API_SECRET_PROP_NAME, CALL_APP_SECRET);
        var client = new DefaultClient(properties);
        DefaultClient.setInstance(client);
        log.info(String.format("SERCH::: (AGORA INITIALIZATION) Agora initialized with api key %s", client.getApiKey()));

        return client;
    }

    @Bean
    public MailerSend send() {
        MailerSend ms = new MailerSend();
        ms.setToken(MAIL_API_KEY);

        log.info(String.format("SERCH::: (MailerSend) Initialized with token %s", ms.getToken()));
        return ms;
    }

    @Bean
    public SmsConfig smsConfig() {
        Twilio.init(SMS_SECRET, SMS_AUTH_TOKEN);
        log.info(String.format("SERCH::: (Twilio) Initialized with account %s", SMS_SECRET));

        SmsConfig config = new SmsConfig();
        config.setPhoneNumber(SMS_PHONE_NUMBER);

        return config;
    }
}