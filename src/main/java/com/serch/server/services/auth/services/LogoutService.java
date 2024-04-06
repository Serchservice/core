package com.serch.server.services.auth.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serch.server.bases.ApiResponse;
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
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final SessionService sessionService;
    private final UserDetailsService userDetailsService;

    @SneakyThrows
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String header = request.getHeader("Authorization");

        if(header == null || !header.startsWith("Bearer")){
            return;
        }

        var res = sessionService.validateSession(header.substring(7));
        if(res.getCode() == 200) {
            authenticate(request, res);
            sessionService.signOut();

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_OK);

            Map<String, Object> data = new HashMap<>();
            data.put("status", HttpServletResponse.SC_OK);
            data.put("path", request.getServletPath());

            ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                    "Sign out successful. You will be redirected to home soon.",
                    data, HttpStatus.OK
            );

            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), apiResponse);
        }
    }

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
