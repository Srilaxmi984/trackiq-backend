package com.trackiq.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // ✅ ADD
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // ✅ ADD

import com.trackiq.backend.security.JwtFilter; // ✅ ADD

import java.util.List;

@Configuration
@EnableMethodSecurity // ✅ ADD
public class SecurityConfig {

    private final JwtFilter jwtFilter; // ✅ ADD

    public SecurityConfig(JwtFilter jwtFilter) { // ✅ ADD
        this.jwtFilter = jwtFilter;
    }

    // 🔐 Main Security Config
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,JwtFilter jwtFilter) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        // ✅ PUBLIC ROUTE
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        // 🔒 OTHER ROUTES
                        .anyRequest().authenticated()
                )
                // ✅ ADD JWT FILTE
                .addFilterBefore(jwtFilter,org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // 🌍 CORS Configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // 🔐 Password Encoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}