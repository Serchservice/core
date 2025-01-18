package com.serch.server.configurations.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serch.server.core.Logging;
import com.serch.server.core.session.SessionService;
import com.serch.server.core.validator.KeyValidatorService;
import com.serch.server.enums.ServerHeader;
import com.serch.server.exceptions.ApiResponseExceptionHandler;
import com.serch.server.exceptions.auth.AuthException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.ServerHttpAsyncRequestControl;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
    private final SessionService sessionService;
    private final UserDetailsService userDetailsService;
    private final KeyValidatorService keyService;
    private final ApiResponseExceptionHandler handler;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        System.out.printf("Socket Request from remote address: %s for local address: %s%n", request.getRemoteAddress(), request.getLocalAddress());

        request = normalize(request);
        try {
            if(keyService.isSigned(request.getHeaders().getFirst(ServerHeader.SERCH_SIGNED.getValue()))) {
                if(keyService.isDrive(request.getHeaders().getFirst(ServerHeader.DRIVE_API_KEY.getValue()), request.getHeaders().getFirst(ServerHeader.DRIVE_SECRET_KEY.getValue()))) {
                    Logging.logRequest(request, "SIGNED DRIVE HANDSHAKE");

                    return true;
                } else if(keyService.isGuest(request.getHeaders().getFirst(ServerHeader.GUEST_API_KEY.getValue()), request.getHeaders().getFirst(ServerHeader.GUEST_SECRET_KEY.getValue()))) {
                    Logging.logRequest(request, "SIGNED GUEST HANDSHAKE");

                    return true;
                } else {
                    String authHeader = request.getHeaders().getFirst("Authorization");

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        Logging.logRequest(request, "AUTHORIZED HANDSHAKE");

                        String token = authHeader.substring(7);
                        var session = sessionService.validateSession(token, null, null);

                        if (session.getStatus().is2xxSuccessful()) {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(session.getData());
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            sessionService.updateLastSignedIn();

                            attributes.put("username", userDetails.getUsername());

                            return true;
                        }
                    } else {
                        Logging.logRequest(request, "SIGNED HANDSHAKE");
                    }
                }

                return true;
            } else {
                Logging.logRequest(request, "BEFORE HANDSHAKE");
            }
        }  catch (ExpiredJwtException | MalformedJwtException | AuthException | SignatureException
                  | UnsupportedJwtException | IllegalArgumentException | StringIndexOutOfBoundsException e
        ) {
            handleError(response, e);
        }

        return false;
    }

    private ServerHttpRequest normalize(ServerHttpRequest request) {
        URI originalUri = request.getURI();
        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(originalUri).build().getQueryParams();

        // Create a new URI without query parameters
        URI normalizedUri = UriComponentsBuilder.fromUri(originalUri)
                .replaceQuery(null)
                .build()
                .toUri();

        return new ServerHttpRequest() {
            @Override
            public Principal getPrincipal() {
                return request.getPrincipal();
            }

            @Override
            @NonNull
            public InetSocketAddress getLocalAddress() {
                return request.getLocalAddress();
            }

            @Override
            @NonNull
            public InetSocketAddress getRemoteAddress() {
                return request.getRemoteAddress();
            }

            @Override
            @NonNull
            public ServerHttpAsyncRequestControl getAsyncRequestControl(@NonNull ServerHttpResponse response) {
                return request.getAsyncRequestControl(response);
            }

            @Override
            @NonNull
            public InputStream getBody() throws IOException {
                return request.getBody();
            }

            @Override
            @NonNull
            public HttpMethod getMethod() {
                return request.getMethod();
            }

            @Override
            @NonNull
            public URI getURI() {
                // Return the normalized URI
                return normalizedUri;
            }

            @Override
            @NonNull
            public Map<String, Object> getAttributes() {
                return request.getAttributes();
            }

            @Override
            @NonNull
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.addAll(request.getHeaders());

                // Add query parameters to headers if they exist
                if (!queryParams.isEmpty()) {
                    queryParams.forEach((key, values) -> {
                        if (values != null && !values.isEmpty()) {
                            values.forEach(value -> headers.add(key, value));
                        }
                    });
                }

                return headers;
            }
        };
    }

    @SneakyThrows
    private void handleError(ServerHttpResponse response, Exception e) {
        if(response instanceof HttpServletResponse) {
            HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
            servletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            servletResponse.setContentType("application/json");

            new ObjectMapper().writeValue(response.getBody(), handler.handle(e));
        } else {
            response.setStatusCode(HttpStatusCode.valueOf(401));
            new ObjectMapper().writeValue(response.getBody(), handler.handle(e));
        }
    }

    @Override
    public void afterHandshake(@Nullable ServerHttpRequest request, @Nullable ServerHttpResponse response, @Nullable WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}