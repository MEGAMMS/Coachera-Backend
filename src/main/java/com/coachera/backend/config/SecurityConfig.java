package com.coachera.backend.config;

import com.coachera.backend.security.CustomUserDetailsService;
import com.coachera.backend.security.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // This chain only applies to paths starting with /api/
            .securityMatcher("/api/**") 
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/public-data/**").permitAll()
                // IMPORTANT: The .anyRequest() here now only applies to /api/**
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CHAIN 2: For the STATEFUL Admin Dashboard (/admin/** and other web pages)
     * This has lower priority and handles form-based login with sessions.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // This chain applies to all other requests NOT matched by the first chain
            .authorizeHttpRequests(authorize -> authorize
                // Allow access to the login page and static resources
                .requestMatchers("/admin/login", "/css/**", "/images/**").permitAll()
                // All /admin paths (except login) require the user to have ADMIN authority
                .requestMatchers("/admin/**").hasAuthority("ADMIN") 
                // Any other request not matched by the API chain is permitted (e.g., root path)
                .anyRequest().permitAll()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/admin/login") // Our custom login page
                .loginProcessingUrl("/admin/login") // The URL the form will POST to
                .defaultSuccessUrl("/admin/home", true) // On success, go to the dashboard
                .failureUrl("/admin/login?error=true") // On failure, show an error
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout=true")
                .permitAll()
            )
            .authenticationProvider(authenticationProvider()); 
            // NOTE: We do NOT set session management to STATELESS here.

        return http.build();
    }
}
