package com.coachera.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // Allow all origins
                .allowedMethods("*")        // Allow all HTTP methods
                .allowedHeaders("*")         // Allow all headers
                // .exposedHeaders("Authorization") // Expose auth headers to the client
                .allowCredentials(true);    // Disable credentials if not needed
    }
}
