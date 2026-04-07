package com.techcup.techcup_futbol.persistence.mapper;

import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.persistence.entity.MatchEntity;

public class MatchPersistenceMapper {

    private MatchPersistenceMapper() {}


    public static MatchEntity toEntity(Match match) {
        if (match == null) return null;

        MatchEntity entity = new MatchEntity();
        entity.setId(match.getId());
        entity.setLocalTeam(TeamPersistenceMapper.toEntity(match.getLocalTeam()));
        entity.setVisitorTeam(TeamPersistenceMapper.toEntity(match.getVisitorTeam()));
        entity.setDateTime(match.getDateTime());
        entity.setScoreLocal(match.getScoreLocal());
        entity.setScoreVisitor(match.getScoreVisitor());
        entity.setYellowCards(match.getYellowCards());
        entity.setRedCards(match.getRedCards());
        entity.setField(match.getField());
        entity.setStatus(match.getStatus());
        entity.setWinner(TeamPersistenceMapper.toEntity(match.getWinner()));
        entity.setReferee(RefereePersistenceMapper.toEntity(match.getReferee()));

        return entity;
    }

    public static Match toDomain(MatchEntity entity) {
        if (entity == null) return null;

        Match match = new Match();
        match.setId(entity.getId());
        match.setLocalTeam(TeamPersistenceMapper.toDomain(entity.getLocalTeam()));
        match.setVisitorTeam(TeamPersistenceMapper.toDomain(entity.getVisitorTeam()));
        match.setDateTime(entity.getDateTime());
        match.setScoreLocal(entity.getScoreLocal());
        match.setScoreVisitor(entity.getScoreVisitor());
        match.setYellowCards(entity.getYellowCards());
        match.setRedCards(entity.getRedCards());
        match.setField(entity.getField());
        match.setStatus(entity.getStatus());
        match.setWinner(TeamPersistenceMapper.toDomain(entity.getWinner()));
        match.setReferee(RefereePersistenceMapper.toDomain(entity.getReferee()));

        return match;
    }


    public static Match toDomainShallow(MatchEntity entity) {
        if (entity == null) return null;

        Match match = new Match();
        match.setId(entity.getId());
        match.setLocalTeam(TeamPersistenceMapper.toDomain(entity.getLocalTeam()));
        match.setVisitorTeam(TeamPersistenceMapper.toDomain(entity.getVisitorTeam()));
        match.setDateTime(entity.getDateTime());
        match.setScoreLocal(entity.getScoreLocal());
        match.setScoreVisitor(entity.getScoreVisitor());
        match.setYellowCards(entity.getYellowCards());
        match.setRedCards(entity.getRedCards());
        match.setField(entity.getField());
        match.setStatus(entity.getStatus());
        match.setWinner(TeamPersistenceMapper.toDomain(entity.getWinner()));

        return match;
    }
}