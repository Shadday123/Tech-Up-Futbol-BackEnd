package com.techcup.techcup_futbol.controller.mapper;

import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;

public class TeamMapper {

    private TeamMapper() {}

    public static Team toModel(TeamEntity entity) {
        if (entity == null) return null;
        Team team = new Team();
        team.setId(entity.getId());
        team.setTeamName(entity.getTeamName());
        return team;
    }

    public static TeamEntity toEntity(Team model) {
        if (model == null) return null;
        TeamEntity entity = new TeamEntity();
        entity.setId(model.getId());
        entity.setTeamName(model.getTeamName());
        return entity;
    }
}
