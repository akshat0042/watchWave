package org.learn.watchwave.videos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Order(1)
public class VideoSecurityConfig {

    @Bean
    public SecurityFilterChain videoSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/videos/**", "/api/thumbnails/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow all OPTIONS requests
                        .requestMatchers(HttpMethod.GET, "/api/videos/*/stream").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/thumbnails/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/videos/upload").hasRole("CREATOR")
                        .anyRequest().denyAll()
                );
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:8000")); // Use allowedOriginPatterns
        config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Content-Length", "Content-Range")); // Required for streaming
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/videos/**", config);
        source.registerCorsConfiguration("/api/thumbnails/**", config);
        return source;
    }
}
