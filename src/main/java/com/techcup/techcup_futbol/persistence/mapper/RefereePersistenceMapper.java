package com.techcup.techcup_futbol.persistence.mapper;

import com.techcup.techcup_futbol.core.model.Referee;
import com.techcup.techcup_futbol.persistence.entity.RefereeEntity;

import java.util.stream.Collectors;

public class RefereePersistenceMapper {

    private RefereePersistenceMapper() {}

    public static RefereeEntity toEntity(Referee referee) {
        if (referee == null) return null;

        RefereeEntity entity = new RefereeEntity();
        entity.setId(referee.getId());
        entity.setFullname(referee.getFullname());
        entity.setEmail(referee.getEmail());

        return entity;
    }


    public static Referee toDomain(RefereeEntity entity) {
        if (entity == null) return null;

        Referee referee = new Referee();
        referee.setId(entity.getId());
        referee.setFullname(entity.getFullname());
        referee.setEmail(entity.getEmail());

        if (entity.getAssignedMatches() != null) {
            referee.setAssignedMatches(
                    entity.getAssignedMatches().stream()
                            .map(MatchPersistenceMapper::toDomainShallow)
                            .collect(Collectors.toList())
            );
        }

        return referee;
    }
}