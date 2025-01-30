package com.serch.server.configurations.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serch.server.core.Logging;
import com.serch.server.core.session.GuestSessionService;
import com.serch.server.core.session.SessionService;
import com.serch.server.core.validator.EndpointValidatorService;
import com.serch.server.core.validator.KeyValidatorService;
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
    private final GuestSessionService guestSessionService;
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
                    if(isDrivePermitted(request)) {
                        handleDriveAuthorization(request, response, filterChain);
                    } else if(isGuestPermitted(request)) {
                        handleGuestAuthorization(request, response, filterChain);
                    } else if(ServerUtil.ALLOWED_IP_ADDRESSES.contains(request.getRemoteAddr())){
                        Logging.logRequest(request, "Signed Allowed Ip Request");
                        filterChain.doFilter(request, response);
                    } else {
                        handleJwtAuthorization(request, response, filterChain);
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

    private boolean isDrivePermitted(HttpServletRequest request) {
        return keyService.isDrive(
                request.getHeader(ServerHeader.DRIVE_API_KEY.getValue()),
                request.getHeader(ServerHeader.DRIVE_SECRET_KEY.getValue())
        ) && endpointValidator.isDrivePermitted(request.getServletPath());
    }

    @SneakyThrows
    private void handleDriveAuthorization(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        Logging.logRequest(request, "Signed Drive Request");

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                request.getHeader(ServerHeader.DRIVE_API_KEY.getValue()),
                null,
                new ArrayList<>()
        );
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    private boolean isGuestPermitted(HttpServletRequest request) {
        return keyService.isGuest(
                request.getHeader(ServerHeader.GUEST_API_KEY.getValue()),
                request.getHeader(ServerHeader.GUEST_SECRET_KEY.getValue())
        ) && endpointValidator.isGuestPermitted(request.getServletPath());
    }

    @SneakyThrows
    private void handleGuestAuthorization(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        // Extract Session token from the Guest Authorization header
        String auth = request.getHeader(ServerHeader.GUEST_AUTHORIZATION.getValue());

        // If the Authorization header is missing or does not start with "Bearer", proceed to the next filter
        if(auth == null || auth.length() < 7) {
            Logging.logRequest(request, "Signed Guest ApiKey Request");

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    request.getHeader(ServerHeader.GUEST_API_KEY.getValue()),
                    null,
                    new ArrayList<>()
            );
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(token);
        } else {
            Logging.logRequest(request, "Signed Guest Authorized Request");

            var session = guestSessionService.validateSession(auth);
            if(session.getStatus().is2xxSuccessful()) {
                // If the guest is not already authenticated, set up the authentication context
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
        }

        filterChain.doFilter(request, response);
    }

    @SneakyThrows
    private void handleJwtAuthorization(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        // Extract JWT token from the Authorization header
        String header = request.getHeader("Authorization");

        // If the Authorization header is missing or does not start with ServerUtil.AUTH_KEY, proceed to the next filter
        if(header == null || !header.startsWith(ServerUtil.AUTH_KEY) || header.length() < 7){
            Logging.logRequest(request, "Signed No Auth Request");
        } else {
            Logging.logRequest(request, "Signed Auth Request");

            String token = header.substring(7);

            var session = sessionService.validateSession(token, null, null);
            if(session.getStatus().is2xxSuccessful()) {
                if(SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(session.getData());
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    sessionService.updateLastSignedIn();

                    if(request.getRemoteAddr() != null) {
                        sessionService.updateSessionDetails(request.getRemoteAddr(), token);
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}