package com.viannele.classicsputsimply.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/classics").permitAll() // Make /api/v1/classics public
                        .requestMatchers("/api/v1/classics/pdf/{slug}").authenticated() // Secure the PDF endpoint
                        .anyRequest().permitAll() // Allow anything else not matched to be public (or authenticated - check your requirements)
                )
                .oauth2Login(withDefaults()); // Use the lambda-based configuration
        return http.build();
    }
}
