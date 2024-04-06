package com.serch.server.configurations;

import com.serch.server.exceptions.ServerExceptionHandler;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.services.auth.services.SessionService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final SessionService sessionService;
    private final ServerExceptionHandler handler;

    @Override
    @SneakyThrows
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {
        String header = request.getHeader("Authorization");

        if(header == null || !header.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            return;
        }

        try {
            var session = sessionService.validateSession(header.substring(7));
            if(session.getCode() == 200) {
                if(SecurityContextHolder.getContext().getAuthentication() == null){
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(session.getData());
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(token);
                    sessionService.updateLastSeen();
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | MalformedJwtException | AuthException | SignatureException
            | UnsupportedJwtException | IllegalArgumentException e
        ) {
            if(e instanceof AuthException) {
                handler.handleAuthException((AuthException) e);
            } else if(e instanceof  ExpiredJwtException) {
                handler.handleExpiredJwtException();
            } else if(e instanceof  MalformedJwtException) {
                handler.handleMalformedJwtException();
            } else if(e instanceof  SignatureException) {
                handler.handleSignatureException();
            } else if(e instanceof  UnsupportedJwtException) {
                handler.handleUnsupportedJwtException();
            }
        }
    }
}
