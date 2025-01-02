package com.serch.server.configurations.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serch.server.core.Logging;
import com.serch.server.core.session.SessionService;
import com.serch.server.core.validator.endpoint.EndpointValidatorService;
import com.serch.server.core.validator.key.KeyValidatorService;
import com.serch.server.enums.ServerHeader;
import com.serch.server.exceptions.ApiResponseExceptionHandler;
import com.serch.server.exceptions.ServerExceptionHandler;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.utils.ServerUtil;
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

import java.util.ArrayList;

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
    private final UserDetailsService userDetailsService;
    private final EndpointValidatorService endpointValidator;
    private final SessionService sessionService;
    private final KeyValidatorService keyService;
    private final ApiResponseExceptionHandler handler;

    /**
     * Filters each incoming HTTP request and performs JWT authentication.
     *
     * @param request     The HTTP request.
     * @param response    The HTTP response.
     * @param filterChain The filter chain.
     */
    @Override
    @SneakyThrows
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {
        System.out.printf("New Request from ip address: %s for %s%n", request.getRemoteAddr(), request.getServletPath());

        if(endpointValidator.isSocketPermitted(request.getServletPath()) || endpointValidator.isSwaggerPermitted(request.getServletPath())){
            Logging.logRequest(request, "Unsigned request");

            filterChain.doFilter(request, response);
        } else {
            try {
                if(keyService.isSigned(request.getHeader(ServerHeader.SERCH_SIGNED.getValue()))) {
                    Logging.logRequest(request, "Signed request");

                    if(isDrivePermitted(request)) {
                        authenticateApiKeyRequests(request, "Drive Request", ServerHeader.DRIVE_API_KEY);
                        filterChain.doFilter(request, response);
                    } else if(isGuestPermitted(request)) {
                        authenticateApiKeyRequests(request, "Guest Request", ServerHeader.GUEST_API_KEY);
                        filterChain.doFilter(request, response);
                    } else if(ServerUtil.ALLOWED_IP_ADDRESSES.contains(request.getRemoteAddr())){
                        Logging.logRequest(request, "Allowed Ip Request");
                        filterChain.doFilter(request, response);
                    } else {
                        // Extract JWT token from the Authorization header
                        String header = request.getHeader("Authorization");

                        // If the Authorization header is missing or does not start with "Bearer", proceed to the next filter
                        if(header == null || !header.startsWith("Bearer") || header.length() < 7){
                            Logging.logRequest(request, "No Auth Request");
                            filterChain.doFilter(request, response);
                        } else {
                            // Validate the session associated with the JWT token
                            authenticateJwtRequests(request, header);
                            filterChain.doFilter(request, response);
                        }
                    }
                }
            } catch (ExpiredJwtException | MalformedJwtException | AuthException | SignatureException
                     | UnsupportedJwtException | IllegalArgumentException | StringIndexOutOfBoundsException e
            ) {
                SecurityContextHolder.clearContext();
                new ObjectMapper().writeValue(response.getOutputStream(), handler.handle(e));
            }
        }
    }

    private void authenticateJwtRequests(HttpServletRequest request, String header) {
        Logging.logRequest(request, "JWT Request");

        String jwt = header.substring(7);

        var session = sessionService.validateSession(jwt, null, null);
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

                if(request.getRemoteAddr() != null) {
                    sessionService.updateSessionDetails(request.getRemoteAddr(), jwt);
                }
            }
        }
    }

    private boolean isGuestPermitted(HttpServletRequest request) {
        return keyService.isGuest(
                request.getHeader(ServerHeader.GUEST_API_KEY.getValue()),
                request.getHeader(ServerHeader.GUEST_SECRET_KEY.getValue())
        ) && endpointValidator.isGuestPermitted(request.getServletPath());
    }

    private boolean isDrivePermitted(HttpServletRequest request) {
        return keyService.isDrive(
                request.getHeader(ServerHeader.DRIVE_API_KEY.getValue()),
                request.getHeader(ServerHeader.DRIVE_SECRET_KEY.getValue())
        ) && endpointValidator.isDrivePermitted(request.getServletPath());
    }

    private void authenticateApiKeyRequests(HttpServletRequest request, String message, ServerHeader key) {
        Logging.logRequest(request, message);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        request.getHeader(key.getValue()),
                        null,
                        new ArrayList<>()
                )
        );
    }
}