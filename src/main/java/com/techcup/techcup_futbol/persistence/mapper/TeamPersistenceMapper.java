package com.techcup.techcup_futbol.persistence.mapper;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.persistence.entity.PlayerEntity;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;

import java.util.List;
import java.util.stream.Collectors;

public class TeamPersistenceMapper {

    private TeamPersistenceMapper() {}

    public static TeamEntity toEntity(Team team) {
        if (team == null) return null;

        TeamEntity entity = new TeamEntity();
        entity.setId(team.getId());
        entity.setTeamName(team.getTeamName());
        entity.setShieldUrl(team.getShieldUrl());
        entity.setUniformColors(team.getUniformColors());
        entity.setStatus(team.getStatus());

        if (team.getCaptain() != null) {
            entity.setCaptain(PlayerPersistenceMapper.toEntity(team.getCaptain()));
        }

        if (team.getPlayers() != null) {
            List<PlayerEntity> playerEntities = team.getPlayers().stream()
                    .map(PlayerPersistenceMapper::toEntity)
                    .collect(Collectors.toList());
            entity.setPlayers(playerEntities);
        }

        return entity;
    }


    public static Team toDomain(TeamEntity entity) {
        if (entity == null) return null;

        Team team = new Team();
        team.setId(entity.getId());
        team.setTeamName(entity.getTeamName());
        team.setShieldUrl(entity.getShieldUrl());
        team.setUniformColors(entity.getUniformColors());
        team.setStatus(entity.getStatus());

        if (entity.getCaptain() != null) {
            team.setCaptain(PlayerPersistenceMapper.toDomain(entity.getCaptain()));
        }

        if (entity.getPlayers() != null) {
            List<Player> players = entity.getPlayers().stream()
                    .map(PlayerPersistenceMapper::toDomain)
                    .collect(Collectors.toList());
            team.setPlayers(players);
        }

        return team;
    }
}