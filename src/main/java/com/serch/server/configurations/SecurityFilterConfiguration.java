package com.serch.server.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfigurationSource;

import static com.serch.server.enums.auth.Role.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * The SecurityFilterConfiguration class configured security settings for the application,
 * including authentication, authorization, CORS, and exception handling.
 * <p></p>
 * It is annotated with @Configuration to indicate that it defines application beans.
 * Additionally, it enables Spring Security and method-level security enforcement.
 *
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
 * @see org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityFilterConfiguration {
    /**
     * Authentication provider for user authentication.
     */
    private final AuthenticationProvider authenticationProvider;

    /**
     * JWT authentication filter for processing JWT tokens.
     */
    private final JwtAuthenticationFilter jwtFilterConfiguration;

    /**
     * Logout handler for handling user logout.
     */
    private final LogoutHandler logoutHandler;

    /**
     * Authentication entry point for handling authentication failures.
     */
    private final AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * CORS configuration source for cross-origin resource sharing.
     */
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * Configures security filters and rules for HTTP requests.
     *
     * @param httpSecurity The HTTP security configuration.
     * @return The configured security filter chain.
     * @throws Exception If an error occurs while configuring security.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // Disable CSRF protection
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        // Define request matchers and their corresponding authorization rules
                        .requestMatchers(
                                // Public endpoints
                                "/auth/email/**",
                                "/auth/user/**",
                                "/auth/provider/**",
                                "/auth/business/**",
                                "/auth/reset/**",
                                "/auth/session/refresh",
                                "/company/**",
                                "/plan/**",
                                "/auth/guest/**",
                                "/guest/**",
                                "/switch/**",
                                "/trip/shared/**",
                                "/rating/rate/**",
                                "/location/search/**",
                                "/issue/lodge",
                                "/rating/app"
                        ).permitAll()
                        .requestMatchers(
                                // Public endpoints for actuator and server endpoints
                                HttpMethod.GET,
                                "/actuator/**",
                                "/server/**"
                        ).permitAll()
                        .requestMatchers(
                                // Public endpoints for swagger endpoints
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/swagger-ui/**",
                                "configuration/security",
                                "configuration/ui",
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(
                                // Endpoints requiring USER role
                                "/sharing/create",
                                "/bookmark/add",
                                "/bookmark/remove",
                                "/schedule/all/times/**",
                                "/schedule/request",
                                "/schedule/cancel",
                                "/wallet/pay/trip/check",
                                "/trip/request",
                                "/trip/cancel",
                                "/trip/sharing/permit"
                        ).hasAnyRole(USER.name())
                        .requestMatchers(
                                // Endpoints requiring PROVIDER role
                                "/provider/active/**",
                                "/account/additional"
                        ).hasAnyRole(PROVIDER.name())
                        .requestMatchers(
                                // Endpoints requiring PROVIDER or USER roles
                                "/account/delete"
                        ).hasAnyRole(PROVIDER.name(), USER.name())
                        .requestMatchers(
                                // Endpoints requiring PROVIDER, USER and ASSOCIATE_PROVIDER roles
                                "/schedule/decline",
                                "/schedule/accept",
                                "/providers/active/status",
                                "providers/active/toggle",
                                "/trip/accept",
                                "/trip/cancel",
                                "/trip/arrival/announce",
                                "/trip/invite",
                                "/trip/invite/cancel"
                        ).hasAnyRole(ASSOCIATE_PROVIDER.name(), PROVIDER.name())
                        .requestMatchers(
                                // Endpoints requiring PROVIDER, USER and ASSOCIATE_PROVIDER roles
                                "/account/switch",
                                "/bookmark/all",
                                "/schedule/close",
                                "/schedule/start/",
                                "/profile/**",
                                "/trip/end",
                                "/trip/leave"
                        ).hasAnyRole(ASSOCIATE_PROVIDER.name(), PROVIDER.name(), USER.name())
                        .requestMatchers(
                                // Endpoints requiring PROVIDER or BUSINESS roles
                                "/shop/create",
                                "/shop/service/add",
                                "/shop/service/remove",
                                "/shop/all/open",
                                "/shop/all/close",
                                "/shop/status/toggle",
                                "/shop/update",
                                "/subscription/all",
                                "/subscription/unsubscribe",
                                "/subscription",
                                "/wallet/pay/subscription"
                        ).hasAnyRole(PROVIDER.name(), BUSINESS.name())
                        .requestMatchers(
                                // Endpoints requiring BUSINESS role
                                "/business/associate/**",
                                "/business/profiles/**"
                        ).hasAnyRole(BUSINESS.name())
                        .requestMatchers(
                                // Endpoints requiring PROVIDER, BUSINESS and ASSOCIATE_PROVIDER roles
                                "/subscription/check"
                        ).hasAnyRole(ASSOCIATE_PROVIDER.name(), PROVIDER.name(), BUSINESS.name())
                        .requestMatchers(
                                // Endpoints requiring PROVIDER, BUSINESS and USER roles
                                "/wallet",
                                "/wallet/fund/verify",
                                "/wallet/fund",
                                "/wallet/pay/withdraw",
                                "/wallet/update"
                        ).hasAnyRole(USER.name(), PROVIDER.name(), BUSINESS.name())
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                // Configure CORS settings
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // Set session management strategy to stateless (JWT)
                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(STATELESS))
                // Set custom authentication provider
                .authenticationProvider(authenticationProvider)
                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilterConfiguration, UsernamePasswordAuthenticationFilter.class)
                // Configure exception handling
                .exceptionHandling(handler -> handler.authenticationEntryPoint(authenticationEntryPoint))
                // Configure logout handling
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutUrl("/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) ->
                                SecurityContextHolder.clearContext()
                        )
                );
        return httpSecurity.build();
    }
}
