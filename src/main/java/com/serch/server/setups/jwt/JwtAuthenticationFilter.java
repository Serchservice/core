package com.serch.server.setups.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serch.server.setups.handlers.SecurityContextHandler;
import com.serch.server.core.Logging;
import com.serch.server.core.session.GoSessionService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * The JwtAuthenticationFilter class is responsible for authenticating dtos using JWT (JSON Web Token).
 * It extends OncePerRequestFilter, ensuring that it is only executed once per request.
 * <p></p>
 * This filter intercepts incoming dtos, extracts the JWT token from the Authorization header,
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
    private final EndpointValidatorService endpointValidator;
    private final SessionService sessionService;
    private final GuestSessionService guestSessionService;
    private final GoSessionService goSessionService;
    private final KeyValidatorService keyService;
    private final ApiResponseExceptionHandler handler;
    private final SecurityContextHandler securityContextHandler;

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
        String path = request.getRequestURI();
        System.out.printf("New Request from ip address: %s for %s%n", request.getRemoteAddr(), path);

        // Allow public access to LinkedDynamicUrlController (/{shortUrl})
        // Matches "/{shortUrl}" where {shortUrl} is any single path segment
        if (path.matches("^/[^/]+$")) {
            Logging.logRequest(request, "Public access granted for short URL");
            filterChain.doFilter(request, response);
            return;
        }

        if(endpointValidator.isSocketPermitted(path) || endpointValidator.isSwaggerPermitted(path)){
            Logging.logRequest(request, "Unsigned request");
            filterChain.doFilter(request, response);
        } else {
            try {
                if(keyService.isSigned(request.getHeader(ServerHeader.SERCH_SIGNED.getValue()))) {
                    if(endpointValidator.isGoAllowedOnly(path)) {
                        if(isDrivePermitted(request)) {
                            if(getGo(request) != null && getGo(request).length() > 7) {
                                handleDriveAuthentication(request, response, filterChain);
                            } else {
                                handleDriveAuthorization(request, response, filterChain);
                            }
                        } else {
                            throw new AuthException("Access is granted to nearby application usage only");
                        }
                    } else if(endpointValidator.isGoAuthenticatedOnly(path)) {
                        if(isDrivePermitted(request)) {
                            handleDriveAuthentication(request, response, filterChain);
                        } else {
                            throw new AuthException("You must be logged in to continue");
                        }
                    } else if(isDrivePermitted(request) && endpointValidator.isDrivePermitted(path)) {
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
        );
    }

    @SneakyThrows
    private void handleDriveAuthorization(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        Logging.logRequest(request, "Signed Drive Request");
        securityContextHandler.setup(request, ServerHeader.DRIVE_API_KEY.getValue());
        filterChain.doFilter(request, response);
    }

    private String getGo(HttpServletRequest request) {
        return request.getHeader(ServerHeader.GO_AUTHORIZATION.getValue());
    }

    @SneakyThrows
    private void handleDriveAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        if(getGo(request) != null && getGo(request).length() > 7) {
            Logging.logRequest(request, "Auth Signed Drive Request");
            securityContextHandler.setup(
                    goSessionService.validateSession(getGo(request)),
                    request,
                    false,
                    true,
                    getGo(request)
            );
        }

        filterChain.doFilter(request, response);
    }

    private boolean isGuestPermitted(HttpServletRequest request) {
        return keyService.isGuest(
                request.getHeader(ServerHeader.GUEST_API_KEY.getValue()),
                request.getHeader(ServerHeader.GUEST_SECRET_KEY.getValue())
        ) && endpointValidator.isGuestPermitted(request.getRequestURI());
    }

    private String getGuest(HttpServletRequest request) {
        return request.getHeader(ServerHeader.GUEST_AUTHORIZATION.getValue());
    }

    @SneakyThrows
    private void handleGuestAuthorization(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        // If the Authorization header is missing or does not start with "Bearer", proceed to the next filter
        if(getGuest(request) == null || getGuest(request).length() < 7) {
            Logging.logRequest(request, "Signed Guest ApiKey Request");
            securityContextHandler.setup(request, ServerHeader.GUEST_API_KEY.getValue());
        } else {
            Logging.logRequest(request, "Signed Guest Authorized Request");
            securityContextHandler.setup(
                    guestSessionService.validateSession(getGuest(request)),
                    request,
                    false,
                    false,
                    getGuest(request)
            );
        }

        filterChain.doFilter(request, response);
    }

    private String getJwt(HttpServletRequest request) {
        return request.getHeader(ServerHeader.AUTHORIZATION.getValue());
    }

    @SneakyThrows
    private void handleJwtAuthorization(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        // If the Authorization header is missing or does not start with ServerUtil.AUTH_KEY, proceed to the next filter
        if(getJwt(request) == null || !getJwt(request).startsWith(ServerUtil.AUTH_KEY) || getJwt(request).length() < 7){
            Logging.logRequest(request, "Signed No Auth Request");
        } else {
            Logging.logRequest(request, "Signed Auth Request");
            securityContextHandler.setup(
                    sessionService.validateSession(getJwt(request).substring(7), null, null),
                    request,
                    true,
                    false,
                    getJwt(request).substring(7)
            );
        }

        filterChain.doFilter(request, response);
    }
}