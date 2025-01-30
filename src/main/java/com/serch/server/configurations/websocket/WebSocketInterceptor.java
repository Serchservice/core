package com.serch.server.configurations.websocket;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.session.GuestSessionService;
import com.serch.server.core.session.SessionService;
import com.serch.server.enums.ServerHeader;
import com.serch.server.utils.ServerUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {
    private static final Logger log = LoggerFactory.getLogger(WebSocketInterceptor.class);

    private final SessionService sessionService;
    private final GuestSessionService guestSessionService;
    private final UserDetailsService userDetailsService;

    @Override
    @SuppressWarnings("unchecked")
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        Optional<Map<String, List<Object>>> headers = Optional.ofNullable(accessor.getMessageHeaders().get("nativeHeaders"))
                .filter(Map.class::isInstance)
                .map(map -> (Map<String, List<Object>>) map);

        if(headers.isPresent()) {
            handleJwtAuthorization(headers.get(), accessor);
            handleGuestAuthorization(headers.get(), accessor);
        }

        logInformation(accessor);

        return message;
    }

    private void handleJwtAuthorization(Map<String, List<Object>> headers, SimpMessageHeaderAccessor accessor) {
        Optional<UserDetails> user = Optional.ofNullable(headers.get(ServerHeader.AUTHORIZATION.getValue()))
                .flatMap(list -> list.stream().findFirst())
                .map(Object::toString)
                .filter(bearer -> bearer.startsWith(ServerUtil.AUTH_KEY))
                .map(bearer -> bearer.substring(7))
                .flatMap(token -> Optional.ofNullable(sessionService.validateSession(token, null, null)))
                .filter(session -> session.getStatus().is2xxSuccessful())
                .map(session -> userDetailsService.loadUserByUsername(session.getData()));

        if(user.isPresent()) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user.get(),
                    null,
                    user.get().getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            sessionService.updateLastSignedIn();

            Map<String, Object> attributes = accessor.getSessionAttributes();
            if (attributes != null) {
                accessor.getSessionAttributes().computeIfAbsent("username", (value) -> user.get().getUsername());
            } else {
                attributes = new HashMap<>();
                attributes.put("username", user.get().getUsername());
                accessor.setSessionAttributes(attributes);
            }
        }
    }

    private void handleGuestAuthorization(Map<String, List<Object>> headers, SimpMessageHeaderAccessor accessor) {
        Optional<String> guest = Optional.ofNullable(headers.get(ServerHeader.GUEST_AUTHORIZATION.getValue()))
                .flatMap(list -> list.stream().findFirst())
                .map(Object::toString)
                .filter(bearer -> bearer.startsWith(ServerUtil.AUTH_KEY))
                .map(bearer -> bearer.substring(7))
                .flatMap(token -> Optional.ofNullable(guestSessionService.validateSession(token)))
                .filter(session -> session.getStatus().is2xxSuccessful())
                .map(ApiResponse::getData);

        if(guest.isPresent()) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    guest.get(),
                    null,
                    new ArrayList<>()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            Map<String, Object> attributes = accessor.getSessionAttributes();
            if (attributes != null) {
                accessor.getSessionAttributes().computeIfAbsent("username", (value) -> guest.get());
            } else {
                attributes = new HashMap<>();
                attributes.put("username", guest.get());
                accessor.setSessionAttributes(attributes);
            }
        }
    }

    private void logInformation(SimpMessageHeaderAccessor accessor) {
        String prefix = "SERCH WEBSOCKET %s:::";

        System.out.printf("Socket Request from account: %s for path: %s%n", Objects.requireNonNull(accessor.getSessionAttributes()).get("username"), accessor.getDestination());
        Objects.requireNonNull(accessor.getSessionAttributes())
                .forEach((a, b) -> log.info(String.format("%s SESSION ATTRIBUTE::: Key=%s | Value=%s", prefix, a, b)));
    }
}