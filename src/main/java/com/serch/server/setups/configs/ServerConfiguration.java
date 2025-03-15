package com.serch.server.setups.configs;

import com.cloudinary.Cloudinary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.resend.Resend;
import com.serch.server.core.sms.SmsConfig;
import com.serch.server.core.validator.AllowedOriginValidatorService;
import com.serch.server.domains.nearby.repositories.go.GoUserRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.setups.server.NotificationKey;
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
    
    private final AllowedOriginValidatorService originService;
    private final UserRepository userRepository;
    private final GoUserRepository goUserRepository;

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

    @Value("${application.cloudinary.url}")
    private String CLOUDINARY_URL;

    @Value("${application.cloudinary.name}")
    private String CLOUDINARY_NAME;

    @Value("${application.cloudinary.api-key}")
    private String CLOUDINARY_API_KEY;

    @Value("${application.cloudinary.secret-key}")
    private String CLOUDINARY_SECRET_KEY;

    /**
     * Configures a RestTemplate bean for making restful HTTP dtos.
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
    public AuthenticationManager authManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures an AuthenticationProvider bean for authentication.
     *
     * @return An AuthenticationProvider instance.
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());

        return auth;
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
     * Configures a UserDetailsService bean for retrieving user details during authentication.
     *
     * @return A UserDetailsService instance.
     */
    @Bean
    public UserDetailsService goUserDetailsService() {
        return username -> goUserRepository.findByEmailAddressIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", getCorsConfiguration());

        return source;
    }

    private CorsConfiguration getCorsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(originService.getServerOrigins());
        configuration.setAllowedOriginPatterns(originService.getServerOriginPatterns());
        configuration.setAllowedMethods(ServerUtil.METHODS);
        configuration.setAllowedHeaders(ServerUtil.HEADERS);
        configuration.setAllowCredentials(true);

        log.info("SERCH::: SERVER CORS | Allowed Origins | {}", configuration.getAllowedOrigins());
        log.info("SERCH::: SERVER CORS | Allowed Origin Patterns | {}", configuration.getAllowedOriginPatterns());
        log.info("SERCH::: SERVER CORS | Allowed Headers | {}", configuration.getAllowedHeaders());
        log.info("SERCH::: SERVER CORS | Allowed Methods | {}", configuration.getAllowedMethods());
        
        return configuration;
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
            // Convert the location back to a JSON string
            json = objectMapper.writeValueAsString(key);
        } else {
            // Parse the JSON string into a Map
            var account = objectMapper.readValue(NOTIFICATION_SERVICE_KEY, HashMap.class);

            // Convert the location back to a JSON string
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
        FirebaseApp app = FirebaseApp.initializeApp(getFirebaseOptions());
        log.info("SERCH::: Firebase Initialized with app - {}", app.getName());

        return app;
    }

    private FirebaseOptions getFirebaseOptions() {
        return FirebaseOptions.builder().setCredentials(credentials()).build();
    }

    @Bean
    public DefaultClient defaultClient() {
        var client = new DefaultClient(getStreamProperties());
        DefaultClient.setInstance(client);
        log.info("SERCH::: (AGORA INITIALIZATION) Agora initialized with api key {}", client.getApiKey());

        return client;
    }

    private Properties getStreamProperties() {
        var properties = new Properties();
        properties.put(DefaultClient.API_KEY_PROP_NAME, CALL_APP_ID);
        properties.put(DefaultClient.API_SECRET_PROP_NAME, CALL_APP_SECRET);

        return properties;
    }

    @Bean
    public Resend mailer() {
        Resend resend = new Resend(MAIL_API_KEY);

        log.info("SERCH::: (Resend) Initialized");
        return resend;
    }

    @Bean
    public SmsConfig smsConfig() {
        Twilio.init(SMS_SECRET, SMS_AUTH_TOKEN);
        log.info("SERCH::: (Twilio) Initialized with account {}", SMS_SECRET);

        SmsConfig config = new SmsConfig();
        config.setPhoneNumber(SMS_PHONE_NUMBER);

        return config;
    }

    @Bean
    public Cloudinary cloudinary() {
        Cloudinary cloud;

        if((CLOUDINARY_NAME != null && !CLOUDINARY_NAME.isEmpty())
                && (CLOUDINARY_API_KEY != null && !CLOUDINARY_API_KEY.isEmpty())
                && (CLOUDINARY_SECRET_KEY != null && !CLOUDINARY_SECRET_KEY.isEmpty())
        ) {
            com.cloudinary.Configuration config = new com.cloudinary.Configuration();
            config.apiKey = CLOUDINARY_API_KEY;
            config.secure = true;
            config.apiSecret = CLOUDINARY_SECRET_KEY;
            config.cloudName = CLOUDINARY_NAME;

            cloud = new Cloudinary(config);
        } else {
            cloud = new Cloudinary(CLOUDINARY_URL);
        }

        log.info("Cloudinary is now initialized with {}", cloud.config.cloudName);

        return cloud;
    }
}