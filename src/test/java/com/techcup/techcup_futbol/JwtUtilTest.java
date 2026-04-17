package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    // Clave suficientemente larga para HS256 (mínimo 32 bytes)
    private static final String SECRET = "test-super-secret-key-1234567890-abcdef";
    private static final long EXPIRATION = 3600000L; // 1 hora

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", EXPIRATION);
    }

    private UserDetails buildUser(String email, String role) {
        return new User(email, "password", List.of(new SimpleGrantedAuthority(role)));
    }

    @Test
    void generateToken_fromUserDetails_returnsNonNullToken() {
        UserDetails user = buildUser("test@example.com", "ROLE_JUGADOR");

        String token = jwtUtil.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateToken_fromEmailAndRole_returnsNonNullToken() {
        String token = jwtUtil.generateToken("admin@example.com", "ROLE_ORGANIZADOR");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsername_returnsCorrectEmail() {
        UserDetails user = buildUser("juan@example.com", "ROLE_JUGADOR");
        String token = jwtUtil.generateToken(user);

        String extracted = jwtUtil.extractUsername(token);

        assertEquals("juan@example.com", extracted);
    }

    @Test
    void isTokenValid_withUserDetails_validToken_returnsTrue() {
        UserDetails user = buildUser("test@example.com", "ROLE_JUGADOR");
        String token = jwtUtil.generateToken(user);

        assertTrue(jwtUtil.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_withUserDetails_wrongUser_returnsFalse() {
        UserDetails user1 = buildUser("user1@example.com", "ROLE_JUGADOR");
        UserDetails user2 = buildUser("user2@example.com", "ROLE_JUGADOR");
        String token = jwtUtil.generateToken(user1);

        assertFalse(jwtUtil.isTokenValid(token, user2));
    }

    @Test
    void isTokenValid_withoutUserDetails_validToken_returnsTrue() {
        String token = jwtUtil.generateToken("test@example.com", "ROLE_JUGADOR");

        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_withoutUserDetails_nullToken_returnsFalse() {
        assertFalse(jwtUtil.isTokenValid(null));
    }

    @Test
    void isTokenValid_withoutUserDetails_blankToken_returnsFalse() {
        assertFalse(jwtUtil.isTokenValid("   "));
    }

    @Test
    void isTokenValid_withoutUserDetails_invalidToken_returnsFalse() {
        assertFalse(jwtUtil.isTokenValid("token.invalido.aqui"));
    }

    @Test
    void isTokenValid_expiredToken_returnsFalse() {
        // Configurar expiración en el pasado
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", -1000L);
        String expiredToken = jwtUtil.generateToken("expired@example.com", "ROLE_JUGADOR");

        assertFalse(jwtUtil.isTokenValid(expiredToken));
    }

    @Test
    void generateToken_emailRole_extractUsernameMatchesEmail() {
        String email = "org@techcup.com";
        String token = jwtUtil.generateToken(email, "ROLE_ORGANIZADOR");

        assertEquals(email, jwtUtil.extractUsername(token));
    }
}
