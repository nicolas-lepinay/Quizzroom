package com.ynov.kiwi.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // toutes les routes
                .allowedOrigins("http://localhost:3000", "http://localhost:3001") // Port front
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

