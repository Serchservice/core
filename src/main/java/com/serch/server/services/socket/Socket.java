package com.serch.server.services.socket;

import com.serch.server.services.auth.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Socket implements SocketService {
    private final SessionService sessionService;
    private final UserDetailsService userDetailsService;

    @Override
    public void authenticate(SimpMessageHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                var session = sessionService.validateSession(token);
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
