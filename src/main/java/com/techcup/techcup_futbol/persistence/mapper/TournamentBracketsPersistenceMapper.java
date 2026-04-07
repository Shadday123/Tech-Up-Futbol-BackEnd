package com.techcup.techcup_futbol.persistence.mapper;

import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.model.TournamentBrackets;
import com.techcup.techcup_futbol.persistence.entity.MatchEntity;
import com.techcup.techcup_futbol.persistence.entity.TournamentBracketsEntity;

import java.util.List;
import java.util.stream.Collectors;

public class TournamentBracketsPersistenceMapper {

    private TournamentBracketsPersistenceMapper() {}

    // ── Domain → Entity ──

    public static TournamentBracketsEntity toEntity(TournamentBrackets brackets) {
        if (brackets == null) return null;

        TournamentBracketsEntity entity = new TournamentBracketsEntity();
        entity.setId(brackets.getId());
        entity.setTournament(TournamentPersistenceMapper.toEntity(brackets.getTournament()));
        entity.setPhase(brackets.getPhase());

        if (brackets.getMatches() != null) {
            List<MatchEntity> matchEntities = brackets.getMatches().stream()
                    .map(MatchPersistenceMapper::toEntity)
                    .collect(Collectors.toList());
            entity.setMatches(matchEntities);
        }

        return entity;
    }


    public static TournamentBrackets toDomain(TournamentBracketsEntity entity) {
        if (entity == null) return null;

        TournamentBrackets brackets = new TournamentBrackets();
        brackets.setId(entity.getId());
        brackets.setTournament(TournamentPersistenceMapper.toDomain(entity.getTournament()));
        brackets.setPhase(entity.getPhase());

        if (entity.getMatches() != null) {
            List<Match> matches = entity.getMatches().stream()
                    .map(MatchPersistenceMapper::toDomain)
                    .collect(Collectors.toList());
            brackets.setMatches(matches);
        }

        return brackets;
    }
}