package com.techcup.techcup_futbol.config;

import com.techcup.techcup_futbol.security.JwtAuthEntryPoint;
import com.techcup.techcup_futbol.security.JwtFilter;
import com.techcup.techcup_futbol.security.OAuthSuccessHandler;
import com.techcup.techcup_futbol.core.security.JwtAuthEntryPoint;
import com.techcup.techcup_futbol.core.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final JwtAuthEntryPoint authEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtFilter jwtFilter,
                          JwtAuthEntryPoint authEntryPoint,
                          CorsConfigurationSource corsConfigurationSource,
                          OAuthSuccessHandler oAuthSuccessHandler) {
        this.jwtFilter = jwtFilter;
        this.authEntryPoint = authEntryPoint;
        this.corsConfigurationSource = corsConfigurationSource;
        this.oAuthSuccessHandler = oAuthSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(authEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/players/registro").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/tournaments/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/standings/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/brackets/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/players/search").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/tournaments/**").hasRole("ORGANIZADOR")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/tournaments/**").hasRole("ORGANIZADOR")
                        .requestMatchers("/api/referees/**").hasRole("ORGANIZADOR")
                        .requestMatchers(HttpMethod.POST,
                                "/api/brackets/**").hasRole("ORGANIZADOR")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/brackets/**").hasRole("ORGANIZADOR")
                        .requestMatchers(HttpMethod.POST,
                                "/api/teams/**").hasAnyRole("CAPITAN", "ORGANIZADOR")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/teams/**").hasAnyRole("CAPITAN", "ORGANIZADOR")
                        .requestMatchers("/api/lineups/**")
                        .hasAnyRole("CAPITAN", "ORGANIZADOR")
                        .requestMatchers("/api/payments/**")
                        .hasAnyRole("CAPITAN", "ORGANIZADOR")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/matches/**")
                        .hasAnyRole("ARBITRO", "ORGANIZADOR")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuthSuccessHandler))
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}