package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.Team;

import java.util.List;

public interface TeamService {

    Team createTeam(Team team);

    void invitePlayer(String teamId, Player player);

    List<Team> getAllTeams();

    Team getTeamById(String id);

    void deleteTeam(String id);
}