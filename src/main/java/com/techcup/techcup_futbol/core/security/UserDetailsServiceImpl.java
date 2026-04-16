package com.techcup.techcup_futbol.core.security;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.SystemRole;
import com.techcup.techcup_futbol.persistence.entity.PlayerEntity;
import com.techcup.techcup_futbol.persistence.entity.UserEntity;
import com.techcup.techcup_futbol.persistence.mapper.PlayerPersistenceMapper;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import com.techcup.techcup_futbol.persistence.repository.UserRepository;
import com.techcup.techcup_futbol.core.util.Base64Util;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(PlayerRepository playerRepository,
                                  UserRepository userRepository) {
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // Primero buscar en players (jugadores)
        Optional<PlayerEntity> playerOpt = playerRepository.findByEmailIgnoreCase(email);
        if (playerOpt.isPresent()) {
            Player player = PlayerPersistenceMapper.toDomain(playerOpt.get());
            SystemRole systemRole = player.getSystemRole() != null
                    ? player.getSystemRole() : SystemRole.JUGADOR;
            String password = player.getPasswordHash() != null
                    ? Base64Util.decode(player.getPasswordHash()) : "";
            return new User(
                    player.getEmail(),
                    password,
                    List.of(new SimpleGrantedAuthority("ROLE_" + systemRole.name()))
            );
        }

        // Luego buscar en users (árbitros, organizadores, admins)
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe usuario con email: " + email));

        return new User(
                userEntity.getEmail(),
                Base64Util.decode(userEntity.getPasswordHash()),
                List.of(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name()))
        );
    }
}
