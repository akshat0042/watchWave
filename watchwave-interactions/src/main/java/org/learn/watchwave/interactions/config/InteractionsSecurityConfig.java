package org.learn.watchwave.interactions.config;

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
@Order(2) // Lower priority than video module if both are present
public class InteractionsSecurityConfig {

    @Bean
    public SecurityFilterChain interactionsSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/api/videos/**/likes/**",
                        "/api/videos/**/comments/**",
                        "/api/users/**/watch-later/**"
                )
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Likes endpoints
                        .requestMatchers(HttpMethod.GET, "/api/videos/**/likes/count").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/videos/**/likes/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/videos/**/likes").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/videos/**/likes").authenticated()

                        // Comments endpoints
                        .requestMatchers(HttpMethod.GET, "/api/videos/**/comments").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/videos/**/comments/**/replies").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/videos/**/comments").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/videos/**/comments/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/users/**/watch-later").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/**/watch-later/contains/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/users/**/watch-later").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**/watch-later/**").authenticated()

                        .anyRequest().denyAll()
                );
        return http.build();
    }
}
