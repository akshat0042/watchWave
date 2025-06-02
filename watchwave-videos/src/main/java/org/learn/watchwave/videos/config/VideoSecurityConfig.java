package org.learn.watchwave.videos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class VideoSecurityConfig {

    @Bean
    public SecurityFilterChain videoSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz
                // Public video viewing - anyone can access
                .requestMatchers(HttpMethod.GET, "/api/videos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/thumbnails/**").permitAll()

                // Video upload - CREATOR role only
                .requestMatchers(HttpMethod.POST, "/api/videos/upload").hasRole("CREATOR")

                // Video management - CREATOR or ADMIN
                .requestMatchers(HttpMethod.PUT, "/api/videos/**").hasAnyRole("CREATOR", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/videos/**").hasAnyRole("CREATOR", "ADMIN")

                // User's own videos - authenticated users
                .requestMatchers(HttpMethod.GET, "/api/videos/my").authenticated()

                .anyRequest().authenticated()
        );

        return http.build();
    }
}
