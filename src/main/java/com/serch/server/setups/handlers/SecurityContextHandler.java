package com.serch.server.setups.handlers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.session.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class SecurityContextHandler {
    private final UserDetailsService userDetailsService;
    private final UserDetailsService goUserDetailsService;
    private final SessionService sessionService;

    public SecurityContextHandler setup(ApiResponse<String> session, HttpServletRequest request, boolean isUser, boolean isGo, String auth) {
        if(isUser || isGo) {
            return handleUserSetup(session, request, auth, isUser);
        } else {
            return handleSetup(session, request);
        }
    }

    public SecurityContextHandler setup(HttpServletRequest request, String key) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                request.getHeader(key),
                null,
                new ArrayList<>()
        );
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);

        return this;
    }

    private SecurityContextHandler handleUserSetup(ApiResponse<String> session, HttpServletRequest request, String token, boolean isUser) {
        if(session.getStatus().is2xxSuccessful()) {
            if(SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = isUser
                        ? this.userDetailsService.loadUserByUsername(session.getData())
                        : this.goUserDetailsService.loadUserByUsername(session.getData());

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

                if(isUser) {
                    sessionService.updateLastSignedIn();
                    if(request.getRemoteAddr() != null) {
                        sessionService.updateSessionDetails(request.getRemoteAddr(), token);
                    }
                }
            }
        }

        return this;
    }

    private SecurityContextHandler handleSetup(ApiResponse<String> session, HttpServletRequest request) {
        if(session.getStatus().is2xxSuccessful()) {
            if(SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        session.getData(),
                        null,
                        new ArrayList<>()
                );
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }

        return this;
    }
}