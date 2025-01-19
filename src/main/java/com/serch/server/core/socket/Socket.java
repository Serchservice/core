package com.serch.server.core.socket;

import com.serch.server.core.session.SessionService;
import com.serch.server.enums.ServerHeader;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class Socket implements SocketService {
    private static final Logger log = LoggerFactory.getLogger(Socket.class);

    private final SessionService sessionService;
    private final UserDetailsService userDetailsService;

    @Override
    public void check(SimpMessageHeaderAccessor access) {
        registerBearerAuthorization(access);

        System.out.printf("Socket Request from user: %s for path: %s%n", Objects.requireNonNull(access.getSessionAttributes()).get("username"), access.getDestination());

        String prefix = "SERCH WEBSOCKET %s:::";

        log.info(String.format("%s Header ID::: %s", prefix, access.getId()));
        log.info(String.format("%s Subscription ID::: %s", prefix, access.getSubscriptionId()));
        log.info(String.format("%s Destination::: %s", prefix, access.getDestination()));
        log.info(String.format("%s Message Type::: %s", prefix, access.getMessageType()));
        log.info(String.format("%s Session ID::: %s", prefix, access.getSessionId()));
        log.info(String.format("%s Content Type::: %s", prefix, access.getContentType()));
        log.info(String.format("%s Timestamp::: %s", prefix, access.getTimestamp()));
        log.info(String.format("%s Error channel::: %s", prefix, access.getErrorChannel()));
        log.info(String.format("%s Reply Channel::: %s", prefix, access.getReplyChannel()));
        Objects.requireNonNull(access.getSessionAttributes())
                .forEach((a, b) -> log.info(String.format("%s SESSION ATTRIBUTE::: Key=%s | Value=%s", prefix, a, b)));
    }

    @SuppressWarnings("unchecked")
    private void registerBearerAuthorization(SimpMessageHeaderAccessor access) {
        Optional.ofNullable(access.getMessageHeaders().get("nativeHeaders"))
                .filter(Map.class::isInstance)
                .map(map -> (Map<String, List<Object>>) map)
                .flatMap(map -> Optional.ofNullable(map.get(ServerHeader.AUTHORIZATION.getValue())))
                .flatMap(list -> list.stream().findFirst())
                .map(Object::toString)
                .filter(bearer -> bearer.startsWith("Bearer "))
                .map(bearer -> bearer.substring(7))
                .flatMap(token -> Optional.ofNullable(sessionService.validateSession(token, null, null)))
                .filter(session -> session.getStatus().is2xxSuccessful())
                .map(session -> userDetailsService.loadUserByUsername(session.getData()))
                .ifPresent(userDetails -> {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    sessionService.updateLastSignedIn();

                    Map<String, Object> attributes = access.getSessionAttributes();
                    if (attributes != null) {
                        access.getSessionAttributes().computeIfAbsent("username", (value) -> userDetails.getUsername());
                    } else {
                        attributes = new HashMap<>();
                        attributes.put("username", userDetails.getUsername());
                        access.setSessionAttributes(attributes);
                    }
                });
    }
}