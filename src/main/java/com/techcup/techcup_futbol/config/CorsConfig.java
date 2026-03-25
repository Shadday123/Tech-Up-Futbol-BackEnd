package com.techcup.techcup_futbol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes permitidos — el frontend puede hacer peticiones
        config.setAllowedOriginPatterns(List.of("*"));

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers que puede enviar el frontend
        config.setAllowedHeaders(List.of(
                "Authorization", "Content-Type", "Accept"));

        // Headers que el frontend puede leer de la respuesta
        config.setExposedHeaders(List.of("Authorization"));

        // Permitir cookies y headers de autenticación
        config.setAllowCredentials(true);

        // El navegador cachea la respuesta del preflight por 1 hora
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}