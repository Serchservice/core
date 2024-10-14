package com.serch.server.core.socket;

import com.serch.server.core.session.SessionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public void authenticate(SimpMessageHeaderAccessor accessor) {
        log.info(String.format("%s::: %s", "SOCKET AUTHENTICATE - DESTINATION", accessor.getDestination()));
        log.info(String.format("%s::: %s", "SOCKET AUTHENTICATE - SESSION ID", accessor.getSessionId()));
        log.info(String.format("%s::: %s", "SOCKET AUTHENTICATE - SUBSCRIPTION ID", accessor.getSubscriptionId()));
        log.info(String.format("%s::: %s", "SOCKET AUTHENTICATE - ID", accessor.getId()));
        Objects.requireNonNull(accessor.getSessionAttributes()).forEach((a, b) -> log.info(String.format("%s::: Key=%s | Value=%s", "SOCKET ATTRIBUTE", a, b)));

        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                var session = sessionService.validateSession(token, null, null);
                if (session.getStatus().is2xxSuccessful()) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(session.getData());
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    sessionService.updateLastSignedIn();
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
    }
}
