package com.serch.server.core.socket;

import com.serch.server.core.session.SessionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class Socket implements SocketService {
    private static final Logger log = LoggerFactory.getLogger(Socket.class);

    private final SessionService sessionService;
    private final UserDetailsService userDetailsService;

    @Override
    public void check(SimpMessageHeaderAccessor access) {
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

//        String authHeader = access.getFirstNativeHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            try {
//                var session = sessionService.validateSession(token, null, null);
//                if (session.getStatus().is2xxSuccessful()) {
//                    UserDetails userDetails = userDetailsService.loadUserByUsername(session.getData());
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                            userDetails, null, userDetails.getAuthorities()
//                    );
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                    sessionService.updateLastSignedIn();
//                }
//            } catch (Exception e) {
//                SecurityContextHolder.clearContext();
//            }
//        }
    }
}
