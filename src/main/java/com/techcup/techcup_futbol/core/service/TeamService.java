package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.Team;

import java.util.List;
import java.util.Optional;

public interface TeamService {

    Team createTeam(Team team);

    void invitePlayer(String teamId, Player player);
    void removePlayer(String teamId, String playerId);

    void validateTeamForTournament(Team team);

    List<Team> getAllTeams();
    Optional<Team> buscarPorId(String id);
    Team obtenerPorId(String id);

    void deleteTeam(String id);
    boolean existsByTeamName(String teamName);
    List<Team> findByCaptainId(String captainId);

}