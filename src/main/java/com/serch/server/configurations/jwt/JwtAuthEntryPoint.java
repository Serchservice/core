package com.serch.server.configurations.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.Logging;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * The JwtAuthEntryPoint class serves as an entry point for handling authentication failures in the application.
 * It implements the AuthenticationEntryPoint interface,
 * allowing customization of how authentication errors are handled.
 * <p></p>
 * This component is responsible for returning a meaningful response when an unauthenticated user attempts to access
 * protected resources, typically due to an invalid or missing JWT token.
 *
 * @see org.springframework.security.web.AuthenticationEntryPoint
 */
@Component("delegatedAuthEntryPoint")
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    /**
     * Logger for logging unauthorized errors.
     */
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

    /**
     * Handles authentication failures by returning an unauthorized response with appropriate error details.
     *
     * @param request        The HTTP request.
     * @param response       The HTTP response.
     * @param authException  The authentication exception that occurred.
     */
    @Override
    @SneakyThrows
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        Logging.logRequest(request, "JWT AUTH ENTRYPOINT");
        // Log the unauthorized error
        logger.error("Unauthorized error: {}", authException.getMessage());

        // Set response content type and status
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<Map<String, Object>> apiResponse = getApiResponse(request, authException);

        // Serialize ApiResponse object to JSON and write to response output stream
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), apiResponse);
    }

    private ApiResponse<Map<String, Object>> getApiResponse(HttpServletRequest request, AuthenticationException authException) {
        Map<String, Object> data = new HashMap<>();
        data.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        data.put("error", "Unauthorized");
        data.put("message", authException.getMessage());
        data.put("path", request.getServletPath());

        // Create ApiResponse object with error details
        return new ApiResponse<>("Invalid token. Please login", data, HttpStatus.FORBIDDEN);
    }
}