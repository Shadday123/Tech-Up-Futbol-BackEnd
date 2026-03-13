package com.techcup.techcup_futbol.service;

import com.techcup.techcup_futbol.model.Player;
import com.techcup.techcup_futbol.model.Team;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    private final List<Team> teams = new ArrayList<>();

    @Override
    public Team createTeam(Team team) {
        teams.add(team);
        return team;
    }

    @Override
    public void invitePlayer(String teamId, Player player) {

        Team team = getTeamById(teamId);

        if (team != null) {

            if (team.getPlayers() == null) {
                team.setPlayers(new ArrayList<>());
            }

            team.getPlayers().add(player);
        }
    }

    @Override
    public List<Team> getAllTeams() {
        return teams;
    }

    @Override
    public Team getTeamById(String id) {
        return teams.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteTeam(String id) {
        teams.removeIf(t -> t.getId().equals(id));
    }
}