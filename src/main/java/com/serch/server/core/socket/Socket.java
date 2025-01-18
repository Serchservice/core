package com.serch.server.core.socket;

import com.serch.server.core.session.SessionService;
import com.serch.server.enums.ServerHeader;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private void registerBearerAuthorization(SimpMessageHeaderAccessor access) {
        try {
            String HEADER_KEY = "nativeHeaders";

            if (access.getMessageHeaders().containsKey(HEADER_KEY) && access.getMessageHeaders().get(HEADER_KEY) instanceof Map) {
                // Safely parse the nativeHeaders map
                @SuppressWarnings("unchecked")
                Map<String, List<Object>> nativeHeaders = (Map<String, List<Object>>) access.getMessageHeaders().get(HEADER_KEY);

                // Extract the Authorization header
                if (nativeHeaders != null && nativeHeaders.containsKey(ServerHeader.AUTHORIZATION.getValue())) {
                    List<Object> authHeaders = nativeHeaders.get(ServerHeader.AUTHORIZATION.getValue());
                    if (authHeaders != null && !authHeaders.isEmpty()) {
                        String bearer = String.valueOf(authHeaders.getFirst());

                        // Validate and process the Bearer token
                        if (bearer.startsWith("Bearer ")) {
                            String token = bearer.substring(7);

                            // Validate the session and authenticate the user
                            var session = sessionService.validateSession(token, null, null);
                            if (session.getStatus().is2xxSuccessful()) {
                                UserDetails userDetails = userDetailsService.loadUserByUsername(session.getData());
                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                sessionService.updateLastSignedIn();

                                // Manage session attributes
                                Map<String, Object> attributes = access.getSessionAttributes();
                                if (attributes != null) {
                                    access.getSessionAttributes().computeIfAbsent("username", (value) -> userDetails.getUsername());
                                } else {
                                    attributes = new HashMap<>();
                                    attributes.put("username", userDetails.getUsername());
                                    access.setSessionAttributes(attributes);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { }
    }
}
