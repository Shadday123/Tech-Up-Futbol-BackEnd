package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.SystemRole;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import com.techcup.techcup_futbol.core.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

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
        StudentPlayer player = buildPlayer("J001", "test@escuelaing.edu.co",
                SystemRole.JUGADOR, "$2a$10$hash");
        when(playerRepository.findByEmailIgnoreCase("test@escuelaing.edu.co"))
                .thenReturn(Optional.of(player));

        UserDetails result = userDetailsService.loadUserByUsername("test@escuelaing.edu.co");

        assertEquals("test@escuelaing.edu.co", result.getUsername());
        assertEquals("$2a$10$hash", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_JUGADOR")));
    }

    @Test
    void loadUserByUsername_withOrganizador_returnsCorrectRole() {
        StudentPlayer player = buildPlayer("J-ORG", "org@escuelaing.edu.co",
                SystemRole.ORGANIZADOR, "$2a$10$hash");
        when(playerRepository.findByEmailIgnoreCase("org@escuelaing.edu.co"))
                .thenReturn(Optional.of(player));

        UserDetails result = userDetailsService.loadUserByUsername("org@escuelaing.edu.co");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZADOR")));
    }

    @Test
    void loadUserByUsername_withNullRole_defaultsToJugador() {
        StudentPlayer player = buildPlayer("J002", "test2@escuelaing.edu.co", null, "pass");
        when(playerRepository.findByEmailIgnoreCase("test2@escuelaing.edu.co"))
                .thenReturn(Optional.of(player));

        UserDetails result = userDetailsService.loadUserByUsername("test2@escuelaing.edu.co");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_JUGADOR")));
    }

    @Test
    void loadUserByUsername_withNullPassword_returnsEmptyString() {
        StudentPlayer player = buildPlayer("J003", "test3@escuelaing.edu.co",
                SystemRole.JUGADOR, null);
        when(playerRepository.findByEmailIgnoreCase("test3@escuelaing.edu.co"))
                .thenReturn(Optional.of(player));

        UserDetails result = userDetailsService.loadUserByUsername("test3@escuelaing.edu.co");

        assertEquals("", result.getPassword());
    }

    @Test
    void loadUserByUsername_withNonExistentEmail_throwsException() {
        when(playerRepository.findByEmailIgnoreCase("noexiste@escuelaing.edu.co"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("noexiste@escuelaing.edu.co"));
    }
}
