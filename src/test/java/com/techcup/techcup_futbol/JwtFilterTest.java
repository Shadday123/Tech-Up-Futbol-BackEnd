package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.security.JwtFilter;
import com.techcup.techcup_futbol.core.security.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtFilter jwtFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    private UserDetails buildUser(String email) {
        return new User(email, "pass", List.of(new SimpleGrantedAuthority("ROLE_JUGADOR")));
    }

    @Test
    void doFilter_noAuthorizationHeader_passesThrough() throws ServletException, IOException {
        jwtFilter.doFilter(request, response, filterChain);

        verify(jwtUtil, never()).extractUsername(any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_authHeaderWithoutBearer_passesThrough() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic sometoken");

        jwtFilter.doFilter(request, response, filterChain);

        verify(jwtUtil, never()).extractUsername(any());
    }

    @Test
    void doFilter_validToken_setsAuthentication() throws ServletException, IOException {
        String token = "valid.jwt.token";
        UserDetails user = buildUser("test@example.com");

        request.addHeader("Authorization", "Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(jwtUtil.isTokenValid(token, user)).thenReturn(true);

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    void doFilter_expiredToken_setsAttributeAndClearsContext() throws ServletException, IOException {
        String token = "expired.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenThrow(new ExpiredJwtException(null, null, "expired"));

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(request.getAttribute("jwt.error")).isEqualTo("Token expirado");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_invalidToken_setsAttributeAndClearsContext() throws ServletException, IOException {
        String token = "bad.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenThrow(new JwtException("invalid"));

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(request.getAttribute("jwt.error")).isEqualTo("Token inválido");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_userNotFound_clearsContextAndContinues() throws ServletException, IOException {
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn("ghost@example.com");
        when(userDetailsService.loadUserByUsername("ghost@example.com"))
                .thenThrow(new UsernameNotFoundException("not found"));

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_tokenInvalidForUser_doesNotSetAuthentication() throws ServletException, IOException {
        String token = "mismatched.jwt.token";
        UserDetails user = buildUser("real@example.com");

        request.addHeader("Authorization", "Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn("real@example.com");
        when(userDetailsService.loadUserByUsername("real@example.com")).thenReturn(user);
        when(jwtUtil.isTokenValid(token, user)).thenReturn(false);

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
