package com.techcup.techcup_futbol.security;

import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Player;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // Buscar el jugador en el DataStore por email
        Player player = DataStore.jugadores.values().stream()
                .filter(p -> p.getEmail() != null
                        && p.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe usuario con email: " + email));

        // Asignar rol según si es capitán o no
        String rol = player.isCaptain() ? "ROLE_CAPITAN" : "ROLE_JUGADOR";

        // La contraseña viene del campo passwordHash del Player
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