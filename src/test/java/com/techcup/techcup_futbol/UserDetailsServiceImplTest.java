package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.SystemRole;
import com.techcup.techcup_futbol.persistence.entity.PlayerEntity;
import com.techcup.techcup_futbol.persistence.entity.StudentPlayerEntity;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import com.techcup.techcup_futbol.persistence.repository.UserRepository;
import com.techcup.techcup_futbol.core.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.techcup.techcup_futbol.core.util.Base64Util;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private StudentPlayerEntity buildPlayerEntity(String id, String email, SystemRole role, String passwordHash) {
        StudentPlayerEntity player = new StudentPlayerEntity();
        player.setId(id);
        player.setFullname("Test Player");
        player.setEmail(email);
        player.setNumberID(123456);
        player.setPosition(PositionEnum.Midfielder);
        player.setAge(22);
        player.setSystemRole(role);
        player.setPasswordHash(passwordHash);
        return player;
    }

    private StudentPlayer buildPlayer(String id, String email, SystemRole role, String passwordHash) {
        StudentPlayer player = new StudentPlayer();
        player.setId(id);
        player.setFullname("Test Player");
        player.setEmail(email);
        player.setNumberID(123456);
        player.setPosition(PositionEnum.Midfielder);
        player.setAge(22);
        player.setSystemRole(role);
        player.setPasswordHash(passwordHash);
        return player;
    }

    @Test
    void loadUserByUsername_withExistingPlayer_returnsUserDetails() {
        String encodedHash = Base64Util.encode("$2a$10$hash");
        StudentPlayerEntity playerEntity = buildPlayerEntity("J001", "test@mail.escuelaing.edu.co",
                SystemRole.JUGADOR, encodedHash);
        StudentPlayer player = buildPlayer("J001", "test@mail.escuelaing.edu.co",
                SystemRole.JUGADOR, encodedHash);

        when(playerRepository.findByEmailIgnoreCase("test@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(playerEntity));

        UserDetails result = userDetailsService.loadUserByUsername("test@mail.escuelaing.edu.co");

        assertEquals("test@mail.escuelaing.edu.co", result.getUsername());
        assertEquals("$2a$10$hash", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_JUGADOR")));
        verify(playerRepository).findByEmailIgnoreCase("test@mail.escuelaing.edu.co");
    }

    @Test
    void loadUserByUsername_withOrganizador_returnsCorrectRole() {
        StudentPlayerEntity playerEntity = buildPlayerEntity("J-ORG", "org@mail.escuelaing.edu.co",
                SystemRole.ORGANIZADOR, Base64Util.encode("$2a$10$hash"));
        when(playerRepository.findByEmailIgnoreCase("org@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(playerEntity));

        UserDetails result = userDetailsService.loadUserByUsername("org@mail.escuelaing.edu.co");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZADOR")));
    }

    @Test
    void loadUserByUsername_withNullRole_defaultsToJugador() {
        StudentPlayerEntity playerEntity = buildPlayerEntity("J002", "test2@mail.escuelaing.edu.co", null, "pass");
        when(playerRepository.findByEmailIgnoreCase("test2@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(playerEntity));

        UserDetails result = userDetailsService.loadUserByUsername("test2@mail.escuelaing.edu.co");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_JUGADOR")));
    }

    @Test
    void loadUserByUsername_withNullPassword_returnsEmptyString() {
        StudentPlayerEntity playerEntity = buildPlayerEntity("J003", "test3@mail.escuelaing.edu.co",
                SystemRole.JUGADOR, null);
        when(playerRepository.findByEmailIgnoreCase("test3@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(playerEntity));

        UserDetails result = userDetailsService.loadUserByUsername("test3@mail.escuelaing.edu.co");

        assertEquals("", result.getPassword());
    }

    @Test
    void loadUserByUsername_withNonExistentEmail_throwsException() {
        when(playerRepository.findByEmailIgnoreCase("noexiste@mail.escuelaing.edu.co"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("noexiste@mail.escuelaing.edu.co"));

        assertTrue(exception.getMessage().contains("noexiste@mail.escuelaing.edu.co"));
        verify(playerRepository).findByEmailIgnoreCase("noexiste@mail.escuelaing.edu.co");
    }

    @Test
    void loadUserByUsername_caseInsensitiveEmail_returnsUserDetails() {
        StudentPlayerEntity playerEntity = buildPlayerEntity("J004", "TEST@ESCUELAING.EDU.CO",
                SystemRole.JUGADOR, Base64Util.encode("$2a$10$hash"));
        when(playerRepository.findByEmailIgnoreCase("test@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(playerEntity));

        UserDetails result = userDetailsService.loadUserByUsername("test@mail.escuelaing.edu.co");

        assertEquals("TEST@ESCUELAING.EDU.CO", result.getUsername());
        verify(playerRepository).findByEmailIgnoreCase("test@mail.escuelaing.edu.co");
    }
}
