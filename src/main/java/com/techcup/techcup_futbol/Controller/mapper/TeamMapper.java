package com.techcup.techcup_futbol.Controller.mapper;

import com.techcup.techcup_futbol.Controller.dto.TeamDTO;
import com.techcup.techcup_futbol.core.model.Team;

public class TeamMapper {
    public static Team DTOtoModel(TeamDTO teamDTO){
        if (teamDTO == null){
            return null;
        }
        Team team = new Team();

        team.setId(teamDTO.getId());
        team.setPlayers(teamDTO.getPlayers());
        team.setTeamName(teamDTO.getTeamName());
        team.setCaptain(teamDTO.getCaptain());
        team.setShieldUrl(teamDTO.getShieldUrl());
        team.setUniformColors(teamDTO.getUniformColors());

        return team;
    }

    public static TeamDTO ModeltoDTo(Team team){

        if (team == null){
            return null;
        }
        TeamDTO teamDTO = new TeamDTO();

        teamDTO.setCaptain(team.getCaptain());
        teamDTO.setTeamName(team.getTeamName());
        teamDTO.setPlayers(team.getPlayers());
        teamDTO.setUniformColors(team.getUniformColors());
        teamDTO.setId(team.getId());
        teamDTO.setShieldUrl(team.getShieldUrl());

        return teamDTO;
    }
}
