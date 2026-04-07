package com.techcup.techcup_futbol.persistence.mapper;

import com.techcup.techcup_futbol.core.model.Lineup;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.persistence.entity.LineUpEntity;
import com.techcup.techcup_futbol.persistence.entity.PlayerEntity;

import java.util.List;
import java.util.stream.Collectors;

public class LineupPersistenceMapper {

    private LineupPersistenceMapper() {}


    public static LineUpEntity toEntity(Lineup lineup) {
        if (lineup == null) return null;

        LineUpEntity entity = new LineUpEntity();
        entity.setId(lineup.getId());
        entity.setMatch(MatchPersistenceMapper.toEntity(lineup.getMatch()));
        entity.setTeam(TeamPersistenceMapper.toEntity(lineup.getTeam()));
        entity.setFormation(lineup.getFormation());
        entity.setFieldPositions(lineup.getFieldPositions());

        if (lineup.getStarters() != null) {
            List<PlayerEntity> starters = lineup.getStarters().stream()
                    .map(PlayerPersistenceMapper::toEntity)
                    .collect(Collectors.toList());
            entity.setStarters(starters);
        }

        if (lineup.getSubstitutes() != null) {
            List<PlayerEntity> substitutes = lineup.getSubstitutes().stream()
                    .map(PlayerPersistenceMapper::toEntity)
                    .collect(Collectors.toList());
            entity.setSubstitutes(substitutes);
        }

        return entity;
    }

    public static Lineup toDomain(LineUpEntity entity) {
        if (entity == null) return null;

        Lineup lineup = new Lineup();
        lineup.setId(entity.getId());
        lineup.setMatch(MatchPersistenceMapper.toDomain(entity.getMatch()));
        lineup.setTeam(TeamPersistenceMapper.toDomain(entity.getTeam()));
        lineup.setFormation(entity.getFormation());
        lineup.setFieldPositions(entity.getFieldPositions());

        if (entity.getStarters() != null) {
            List<Player> starters = entity.getStarters().stream()
                    .map(PlayerPersistenceMapper::toDomain)
                    .collect(Collectors.toList());
            lineup.setStarters(starters);
        }

        if (entity.getSubstitutes() != null) {
            List<Player> substitutes = entity.getSubstitutes().stream()
                    .map(PlayerPersistenceMapper::toDomain)
                    .collect(Collectors.toList());
            lineup.setSubstitutes(substitutes);
        }

        return lineup;
    }
}