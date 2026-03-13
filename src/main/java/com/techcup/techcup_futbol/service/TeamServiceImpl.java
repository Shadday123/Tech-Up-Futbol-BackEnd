package com.techcup.techcup_futbol.service;

import com.techcup.techcup_futbol.model.Player;
import com.techcup.techcup_futbol.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);

    private final List<Team> teams = new ArrayList<>();

    @Override
    public Team createTeam(Team team) {
        log.info("Creando equipo: {}", team.getTeamName());
        teams.add(team);
        log.info("Equipo creado exitosamente con id: {}", team.getId());
        return team;
    }

    @Override
    public void invitePlayer(String teamId, Player player) {
        log.info("Invitando jugador id: {} al equipo id: {}", player.getId(), teamId);

        Team team = getTeamById(teamId);
        if (team != null) {
            if (team.getPlayers() == null) {
                team.setPlayers(new ArrayList<>());
            }
            team.getPlayers().add(player);
            log.info("Jugador id: {} agregado al equipo id: {}", player.getId(), teamId);
        } else {
            log.warn("No se encontró equipo con id: {}", teamId);
        }
    }

    @Override
    public List<Team> getAllTeams() {
        log.info("Listando equipos — total: {}", teams.size());
        return teams;
    }

    @Override
    public Team getTeamById(String id) {
        log.info("Buscando equipo con id: {}", id);
        Team team = teams.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (team == null) {
            log.warn("No se encontró equipo con id: {}", id);
        }

        return team;
    }

    @Override
    public void deleteTeam(String id) {
        log.info("Eliminando equipo con id: {}", id);
        teams.removeIf(t -> t.getId().equals(id));
        log.info("Equipo con id: {} eliminado", id);
    }
}