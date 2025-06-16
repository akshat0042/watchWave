package org.learn.watchwave.subscriptions.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Order(3) // Lower priority than video and interactions modules if present
public class SubscriptionsSecurityConfig {

    @Bean
    public SecurityFilterChain subscriptionsSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/users/**/subscriptions/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/**/subscriptions").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**/subscriptions/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/**/subscriptions").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/**/subscriptions/to-me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/**/subscriptions/contains/**").authenticated()

                        .anyRequest().denyAll()
                );
        return http.build();
    }
}
