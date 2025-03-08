package com.serch.server.setups.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.Logging;
import com.serch.server.core.session.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service responsible for handling user logout.
 * It implements its wrapper class {@link org.springframework.security.web.authentication.logout.LogoutHandler}
 *
 * @see SessionService
 * @see UserDetailsService
 */
@Service
@RequiredArgsConstructor
public class LogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {
    private final SessionService sessionService;
    private final UserDetailsService userDetailsService;

    /**
     * Handles user logout by invalidating the session and signing out.
     *
     * @param request        The HTTP request.
     * @param response       The HTTP response.
     * @param authentication The authentication object.
     *
     * @see HttpServletResponse
     * @see HttpServletRequest
     * @see Authentication
     */
    @SneakyThrows
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Logging.logRequest(request, "LOGOUT SERVICE");

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer")) {
            return;
        }

        String jwt = header.substring(7);
        var res = sessionService.validateSession(jwt, null, null);
        if (res.getCode() == 200) {
            authenticate(request, res);

            sessionService.updateSessionDetails(request.getRemoteAddr(), jwt);
            sessionService.signOut(jwt);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_OK);

            Map<String, Object> data = new HashMap<>();
            data.put("status", HttpServletResponse.SC_OK);
            data.put("path", request.getServletPath());

            ApiResponse<Map<String, Object>> message = new ApiResponse<>("Sign out successful.", data, HttpStatus.OK);

            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), message);
        }
    }

    /**
     * Authenticates the user after logout.
     *
     * @param request The HTTP request.
     * @param res     The API response.
     *
     * @see HttpServletRequest
     * @see ApiResponse
     */
    private void authenticate(HttpServletRequest request, ApiResponse<String> res) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(res.getData());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}