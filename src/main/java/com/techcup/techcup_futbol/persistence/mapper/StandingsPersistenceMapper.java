package com.techcup.techcup_futbol.persistence.mapper;

import com.techcup.techcup_futbol.core.model.Standings;
import com.techcup.techcup_futbol.persistence.entity.StandingsEntity;

public class StandingsPersistenceMapper {

    private StandingsPersistenceMapper() {}

    public static StandingsEntity toEntity(Standings standings) {
        if (standings == null) return null;

        StandingsEntity entity = new StandingsEntity();
        entity.setId(standings.getId());
        entity.setTournamentId(standings.getTournamentId());
        entity.setTeam(TeamPersistenceMapper.toEntity(standings.getTeam()));
        entity.setMatchesPlayed(standings.getMatchesPlayed());
        entity.setMatchesWon(standings.getMatchesWon());
        entity.setMatchesDrawn(standings.getMatchesDrawn());
        entity.setMatchesLost(standings.getMatchesLost());
        entity.setGoalsFor(standings.getGoalsFor());
        entity.setGoalsAgainst(standings.getGoalsAgainst());
        entity.setGoalsDifference(standings.getGoalsDifference());
        entity.setPoints(standings.getPoints());

        return entity;
    }


    public static Standings toDomain(StandingsEntity entity) {
        if (entity == null) return null;

        Standings standings = new Standings();
        standings.setId(entity.getId());
        standings.setTournamentId(entity.getTournamentId());
        standings.setTeam(TeamPersistenceMapper.toDomain(entity.getTeam()));
        standings.setMatchesPlayed(entity.getMatchesPlayed());
        standings.setMatchesWon(entity.getMatchesWon());
        standings.setMatchesDrawn(entity.getMatchesDrawn());
        standings.setMatchesLost(entity.getMatchesLost());
        standings.setGoalsFor(entity.getGoalsFor());
        standings.setGoalsAgainst(entity.getGoalsAgainst());
        standings.setGoalsDifference(entity.getGoalsDifference());
        standings.setPoints(entity.getPoints());

        return standings;
    }
}