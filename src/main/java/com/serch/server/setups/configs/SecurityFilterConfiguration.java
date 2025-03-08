package com.serch.server.setups.configs;

import com.serch.server.setups.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;

/**
 * The SecurityFilterConfiguration class configured security settings for the application,
 * including authentication, auth, CORS, and exception handling.
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
    private final DaoAuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtFilterConfiguration;
    private final LogoutHandler logoutHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * Configures security filters and rules for HTTP dtos.
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
                        // Define request matchers and their corresponding auth rules
                        .requestMatchers(
                                // Public endpoints
                                "/api/v1/auth/email/**",
                                "/api/v1/category/**",
                                "/api/v1/auth/user/**",
                                "/api/v1/auth/provider/**",
                                "/api/v1/auth/business/**",
                                "/api/v1/auth/password/reset/**",
                                "/api/v1/auth/session/**",
                                "/api/v1/auth/associate/**",
                                "/api/v1/referral/program/verify/**",
                                "/api/v1/company/**",
                                "/api/v1/auth/guest/**",
                                "/api/v1/issue/lodge",
                                "/api/v1/country/**",
                                "/api/v1/certificate/verify",
                                "/api/v1/verification/receive/**",
                                "/api/v1/call/rtc/**",
                                "/ws:serch/**",
                                "/ws:trip/**",
                                "/api/v1/auth/admin/**",
                                "/api/v1/specialty/all",
                                "/api/v1/organization",
                                "/api/v1/auth/go/**"
                        ).permitAll()
                        .requestMatchers(regexMatcher("^/[^/]+$")).permitAll()
                        .requestMatchers(
                                // Public endpoints for actuator and server endpoints
                                HttpMethod.GET,
                                "/actuator/**",
                                "/api/v1/server/**"
                        ).permitAll()
                        .requestMatchers(
                                // Public endpoints for swagger endpoints
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/swagger-ui/**",
                                "/configuration/security",
                                "/configuration/ui",
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs.yaml",
                                "/v3/api-docs.yaml/",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(
                                // Endpoints requiring USER role
                                "/api/v1/sharing/create",
                                "/api/v1/bookmark/add",
                                "/api/v1/bookmark/remove",
                                "/api/v1/schedule/all/times/**",
                                "/api/v1/schedule/request",
                                "/api/v1/schedule/cancel",
                                "/api/v1/wallet/pay/trip/check",
                                "/api/v1/trip/request",
                                "/api/v1/trip/sharing/permit"
                        ).hasAnyRole(USER.name())
                        .requestMatchers(
                                // Endpoints requiring PROVIDER role
                                "/api/v1/provider/active/**",
                                "/api/v1/account/additional"
                        ).hasAnyRole(PROVIDER.name())
                        .requestMatchers(
                                // Endpoints requiring PROVIDER or USER roles
                                "/api/v1/account/delete"
                        ).hasAnyRole(PROVIDER.name(), USER.name(), BUSINESS.name())
                        .requestMatchers(
                                // Endpoints requiring PROVIDER, USER and ASSOCIATE_PROVIDER roles
                                "/api/v1/schedule/decline",
                                "/api/v1/schedule/accept",
                                "/api/v1/active/status",
                                "/api/v1/active/toggle",
                                "/api/v1/trip/accept",
                                "/api/v1/trip/arrival/announce"
                        ).hasAnyRole(ASSOCIATE_PROVIDER.name(), PROVIDER.name())
                        .requestMatchers(
                                // Endpoints requiring BUSINESS role
                                "/api/v1/subscription/business/**",
                                "/api/v1/profile/business/**"
                        ).hasAnyRole(BUSINESS.name())
                        .requestMatchers(
                                // Endpoints requiring PROVIDER, USER and ASSOCIATE_PROVIDER roles
                                "/api/v1/account/switch",
                                "/api/v1/bookmark/all",
                                "/api/v1/schedule/close",
                                "/api/v1/schedule/start/",
                                "/api/v1/profile/**"
                        ).hasAnyRole(ASSOCIATE_PROVIDER.name(), PROVIDER.name(), USER.name())
                        .requestMatchers(
                                // Endpoints requiring PROVIDER or BUSINESS roles
                                "/api/v1/shop/create",
                                "/api/v1/shop/service/add",
                                "/api/v1/shop/service/remove",
                                "/api/v1/shop/all/open",
                                "/api/v1/shop/all/close",
                                "/api/v1/shop/status/toggle",
                                "/api/v1/shop/update",
                                "/api/v1/subscription/all",
                                "/api/v1/subscription/unsubscribe",
                                "/api/v1/wallet/pay/subscription"
                        ).hasAnyRole(PROVIDER.name(), BUSINESS.name())
                        .requestMatchers(
                                // Endpoints requiring PROVIDER, BUSINESS and ASSOCIATE_PROVIDER roles
                                "/api/v1/subscription"
                        ).hasAnyRole(ASSOCIATE_PROVIDER.name(), PROVIDER.name(), BUSINESS.name())
                        .requestMatchers(
                                // Endpoints requiring PROVIDER, BUSINESS and USER roles
                                "/api/v1/wallet",
                                "/api/v1/wallet/fund/verify",
                                "/api/v1/wallet/fund",
                                "/api/v1/wallet/pay/withdraw",
                                "/api/v1/wallet/update"
                        ).hasAnyRole(USER.name(), PROVIDER.name(), BUSINESS.name())
                        // All other dtos require authentication
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
                        .logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );
        return httpSecurity.build();
    }
}
