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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityFilterConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtFilterConfiguration;
    private final LogoutHandler logoutHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                            "/auth/entry/**",
                            "/auth/user/**",
                            "/auth/provider/**",
                            "/auth/business/**",
                            "/auth/admin/email",
                            "/auth/admin/login",
                            "/auth/admin/register",
                            "/auth/admin/token",
                            "/auth/reset/**",
                            "/session/refresh",
                            "/countries_in_serch/verify",
                            "/countries_in_serch/request",
                            "/account/plan/all",
                            "/account/plan/subscribe",
                            "/product",
                            "/trip/pay/**",
                            "/trip/request",
                            "/trip/cancel",
                            "/trip/end",
                            "/server/**",
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
                                "/sharing/create",
                                "/bookmark/add",
                                "/bookmark/remove"
                        ).hasAnyRole(USER.name())
                        .requestMatchers(
                                "/verify/**",
                                "/verify"
                        ).hasAnyRole(PROVIDER.name(), BUSINESS.name())
                        .requestMatchers(
                                "/provider/active/**"
                        ).hasAnyRole(PROVIDER.name())
                        .requestMatchers(
                                "/business/associates/**",
                                "/business/associates",
                                "/schedule/business",
                                "/schedule/business_associate",
                                "/account/specialty/business/**"
                        ).hasAnyRole(BUSINESS.name())
                        .requestMatchers(
                                HttpMethod.GET,
                                "/actuator/**"
                        )
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilterConfiguration, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handler -> handler.authenticationEntryPoint(authenticationEntryPoint))
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
