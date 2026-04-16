package com.techcup.techcup_futbol.core.security;

import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final PlayerRepository playerRepository;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public OAuthSuccessHandler(JwtUtil jwtUtil, PlayerRepository playerRepository) {
        this.jwtUtil = jwtUtil;
        this.playerRepository = playerRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String token = jwtUtil.generateToken(email, "ROLE_JUGADOR");

        String userId = playerRepository.findByEmailIgnoreCase(email)
                .map(p -> p.getId())
                .orElse("");

        String redirectUrl = frontendUrl + "/oauth2/callback"
                + "?token=" + token
                + "&email=" + email
                + "&rol=JUGADOR"
                + "&userId=" + userId;

        response.sendRedirect(redirectUrl);
    }
}
