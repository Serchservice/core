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

/**
 * The JwtAuthenticationFilter class is responsible for authenticating requests using JWT (JSON Web Token).
 * It extends OncePerRequestFilter, ensuring that it is only executed once per request.
 * <p></p>
 * This filter intercepts incoming requests, extracts the JWT token from the Authorization header,
 * validates the token, loads user details, and sets up the authentication context if the token is valid.
 * It also handles various exceptions related to JWT processing and authentication.
 *
 * @see OncePerRequestFilter
 * @see org.springframework.security.core.userdetails.UserDetailsService
 * @see SessionService
 * @see ServerExceptionHandler
 */
@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    /**
     * Service for retrieving user details.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Service for managing user sessions.
     */
    private final SessionService sessionService;

    /**
     * Handler for server exceptions related to authentication.
     */
    private final ServerExceptionHandler handler;

    /**
     * Filters each incoming HTTP request and performs JWT authentication.
     *
     * @param request     The HTTP request.
     * @param response    The HTTP response.
     * @param filterChain The filter chain.
     */
    @Override
    @SneakyThrows
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {
        // Extract JWT token from the Authorization header
        String header = request.getHeader("Authorization");

        // If the Authorization header is missing or does not start with "Bearer", proceed to the next filter
        if(header == null || !header.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Validate the session associated with the JWT token
            var session = sessionService.validateSession(header.substring(7));
            if(session.getStatus().is2xxSuccessful()) {
                // If the user is not already authenticated, set up the authentication context
                if(SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(session.getData());
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(token);
                    // Update the last seen timestamp of the session
                    sessionService.updateLastSignedIn();
                }
            }
            // Proceed to the next filter
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | MalformedJwtException | AuthException | SignatureException
                 | UnsupportedJwtException | IllegalArgumentException e
        ) {
            // Handle various exceptions related to JWT processing and authentication
            if(e instanceof AuthException) {
                handler.handleAuthException((AuthException) e);
            } else if(e instanceof  ExpiredJwtException) {
                handler.handleExpiredJwtException((ExpiredJwtException) e);
            } else if(e instanceof  MalformedJwtException) {
                handler.handleMalformedJwtException((MalformedJwtException) e);
            } else if(e instanceof  SignatureException) {
                handler.handleSignatureException((SignatureException) e);
            } else if(e instanceof  UnsupportedJwtException) {
                handler.handleUnsupportedJwtException((UnsupportedJwtException) e);
            }
        }
    }
}