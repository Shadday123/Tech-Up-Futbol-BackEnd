package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.Team;

import java.util.List;
import java.util.Optional;

public interface TeamService {

    Team createTeam(Team team);

    void invitePlayer(String teamId, Player player);
    void removePlayer(String teamId, String playerId);   // desvincula jugador del equipo

    void validateTeamForTournament(Team team);           // ahora sí está en la interfaz

    List<Team> getAllTeams();
    Optional<Team> buscarPorId(String id);               // consistente con PlayerService
    Team obtenerPorId(String id);                        // lanza TeamException si no existe

    void deleteTeam(String id);                          // lanza TeamException si no existe
}