package com.techcup.techcup_futbol.core.security;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.SystemRole;
import com.techcup.techcup_futbol.repository.PlayerRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PlayerRepository playerRepository;

    public UserDetailsServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Player player = playerRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe usuario con email: " + email));

        SystemRole systemRole = player.getSystemRole() != null
                ? player.getSystemRole() : SystemRole.JUGADOR;
        String rol = "ROLE_" + systemRole.name();

        String password = player.getPasswordHash() != null
                ? player.getPasswordHash()
                : "";

        return new User(
                player.getEmail(),
                password,
                List.of(new SimpleGrantedAuthority(rol))
        );
    }
}
