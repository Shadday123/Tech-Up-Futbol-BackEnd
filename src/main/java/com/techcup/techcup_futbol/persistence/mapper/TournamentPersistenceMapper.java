package com.techcup.techcup_futbol.persistence.mapper;

import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.persistence.entity.TournamentEntity;

public class TournamentPersistenceMapper {

    private TournamentPersistenceMapper() {}

    public static TournamentEntity toEntity(Tournament tournament) {
        if (tournament == null) return null;

        TournamentEntity entity = new TournamentEntity();
        entity.setId(tournament.getId());
        entity.setName(tournament.getName());
        entity.setStartDate(tournament.getStartDate());
        entity.setEndDate(tournament.getEndDate());
        entity.setRegistrationFee(tournament.getRegistrationFee());
        entity.setMaxTeams(tournament.getMaxTeams());
        entity.setConfigId(tournament.getConfigId());
        entity.setRules(tournament.getRules());
        entity.setRegistrationDeadline(tournament.getRegistrationDeadline());
        entity.setImportantDates(tournament.getImportantDates());
        entity.setMatchSchedules(tournament.getMatchSchedules());
        entity.setFields(tournament.getFields());
        entity.setSanctions(tournament.getSanctions());
        entity.setCurrentState(tournament.getCurrentState());

        return entity;
    }


    public static Tournament toDomain(TournamentEntity entity) {
        if (entity == null) return null;

        Tournament tournament = new Tournament();
        tournament.setId(entity.getId());
        tournament.setName(entity.getName());
        tournament.setStartDate(entity.getStartDate());
        tournament.setEndDate(entity.getEndDate());
        tournament.setRegistrationFee(entity.getRegistrationFee());
        tournament.setMaxTeams(entity.getMaxTeams());
        tournament.setConfigId(entity.getConfigId());
        tournament.setRules(entity.getRules());
        tournament.setRegistrationDeadline(entity.getRegistrationDeadline());
        tournament.setImportantDates(entity.getImportantDates());
        tournament.setMatchSchedules(entity.getMatchSchedules());
        tournament.setFields(entity.getFields());
        tournament.setSanctions(entity.getSanctions());
        tournament.setCurrentState(entity.getCurrentState());

        return tournament;
    }
}