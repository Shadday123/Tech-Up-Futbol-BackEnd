package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.controller.AuthController;
import com.techcup.techcup_futbol.core.model.SystemRole;
import com.techcup.techcup_futbol.core.security.JwtUtil;
import com.techcup.techcup_futbol.persistence.entity.PlayerEntity;
import com.techcup.techcup_futbol.persistence.entity.StudentPlayerEntity;
import com.techcup.techcup_futbol.persistence.entity.UserEntity;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import com.techcup.techcup_futbol.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock AuthenticationManager authManager;
    @Mock JwtUtil jwtUtil;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock PlayerRepository playerRepository;

    @InjectMocks
    private AuthController authController;

    private AuthController.LoginRequest loginRequest;
    private AuthController.OrganizerRegistrationRequest orgRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new AuthController.LoginRequest("test@example.com", "Pass123");
        orgRequest = new AuthController.OrganizerRegistrationRequest(
                "Juan Admin", "admin@example.com", "Admin123", "TECHCUP-ORG-2025");
    }

    // ── LOGIN ──

    @Test
    void login_validCredentials_returnsOkWithToken() {
        UserDetails userDetails = new User("test@example.com", "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_JUGADOR")));
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authManager.authenticate(any())).thenReturn(auth);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt.token.here");
        when(playerRepository.findByEmailIgnoreCase("test@example.com"))
                .thenReturn(Optional.of(new StudentPlayerEntity()));

        ResponseEntity<AuthController.LoginResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt.token.here", response.getBody().token());
        assertEquals("Login exitoso", response.getBody().mensaje());
    }

    @Test
    void login_validCredentials_playerNotFound_fallsBackToUserRepo() {
        UserDetails userDetails = new User("org@example.com", "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_ORGANIZADOR")));
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authManager.authenticate(any())).thenReturn(auth);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt.token.org");
        when(playerRepository.findByEmailIgnoreCase("org@example.com")).thenReturn(Optional.empty());
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());
        when(userRepository.findByEmail("org@example.com")).thenReturn(Optional.of(userEntity));

        ResponseEntity<AuthController.LoginResponse> response = authController.login(
                new AuthController.LoginRequest("org@example.com", "Admin123"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt.token.org", response.getBody().token());
    }

    @Test
    void login_badCredentials_returns401() {
        when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        ResponseEntity<AuthController.LoginResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales incorrectas", response.getBody().mensaje());
        assertNull(response.getBody().token());
    }

    // ── REGISTRO ORGANIZADOR ──

    @Test
    void registrarOrganizador_validTokenAndNewEmail_returnsCreated() {
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Admin123")).thenReturn("encoded");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity u = inv.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });

        ResponseEntity<AuthController.LoginResponse> response =
                authController.registrarOrganizador(orgRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("ORGANIZADOR", response.getBody().rol());
        assertEquals("Organizador registrado exitosamente", response.getBody().mensaje());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void registrarOrganizador_invalidToken_returnsForbidden() {
        AuthController.OrganizerRegistrationRequest badReq =
                new AuthController.OrganizerRegistrationRequest(
                        "Juan", "admin@example.com", "Admin123", "WRONG-TOKEN");

        ResponseEntity<AuthController.LoginResponse> response =
                authController.registrarOrganizador(badReq);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Token de autorización inválido", response.getBody().mensaje());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registrarOrganizador_emailAlreadyExists_returnsConflict() {
        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(new UserEntity()));

        ResponseEntity<AuthController.LoginResponse> response =
                authController.registrarOrganizador(orgRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().mensaje().contains("admin@example.com"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_noAuthoritiesReturnsDefaultRole() {
        UserDetails userDetails = new User("noauth@example.com", "encoded", List.of());
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authManager.authenticate(any())).thenReturn(auth);
        when(jwtUtil.generateToken(userDetails)).thenReturn("token123");
        when(playerRepository.findByEmailIgnoreCase("noauth@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("noauth@example.com")).thenReturn(Optional.empty());

        ResponseEntity<AuthController.LoginResponse> response = authController.login(
                new AuthController.LoginRequest("noauth@example.com", "pass"));

        assertEquals("ROLE_JUGADOR", response.getBody().rol());
    }
}
