package com.serch.server.core.socket;

import com.serch.server.services.auth.services.SessionService;
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
        log.info(accessor.getDestination(), "SOCKET AUTHENTICATE - DESTINATION");
        log.info(accessor.getSessionId(), "SOCKET AUTHENTICATE - SESSION ID");
        log.info(accessor.getSubscriptionId(),  "SOCKET AUTHENTICATE - SUBSCRIPTION ID");
        log.info(String.valueOf(accessor.getId()), "SOCKET AUTHENTICATE - ID");
        Objects.requireNonNull(accessor.getSessionAttributes()).forEach(log::info);

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
