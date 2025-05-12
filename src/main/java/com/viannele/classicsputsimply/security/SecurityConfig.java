package com.viannele.classicsputsimply.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/v1/classics").permitAll() // Make /api/v1/classics public
                .requestMatchers("/logout").permitAll()
                .requestMatchers("/api/v1/user").authenticated()
                .requestMatchers("/api/v1/classics/pdf/{slug}").authenticated() // Secure the PDF endpoint
                .anyRequest().permitAll() // Allow anything else not matched to be public (or authenticated - check your requirements)
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(successHandler)
            );

        // 🔽 Add the JWT filter BEFORE the default Spring authentication filter
        http.addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
