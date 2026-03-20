package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.RefereeDTOs.*;
import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.model.Referee;
import com.techcup.techcup_futbol.exception.RefereeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RefereeServiceImpl implements RefereeService {

    private static final Logger log = LoggerFactory.getLogger(RefereeServiceImpl.class);

    private final Map<String, Referee> referees = new HashMap<>();
    private final Map<String, String> matchRefereeIndex = new HashMap<>();

    @Autowired
    private MatchServiceImpl matchService;

    @Override
    public RefereeResponse create(CreateRefereeRequest request) {
        log.info("Registrando árbitro: {}", request.fullname());

        boolean emailExists = referees.values().stream()
                .anyMatch(r -> r.getEmail().equalsIgnoreCase(request.email()));
        if (emailExists) {
            throw new RefereeException("email",
                    String.format(RefereeException.EMAIL_ALREADY_REGISTERED, request.email()));
        }

        Referee referee = new Referee();
        referee.setId(UUID.randomUUID().toString());
        referee.setFullname(request.fullname());
        referee.setEmail(request.email());
        referee.setAssignedMatches(new ArrayList<>());

        referees.put(referee.getId(), referee);
        log.info("Árbitro registrado ID: {}", referee.getId());
        return toResponse(referee);
    }

    @Override
    public RefereeResponse assignToMatch(String matchId, AssignRefereeRequest request) {
        log.info("Asignando árbitro ID: {} al partido ID: {}", request.refereeId(), matchId);

        Referee referee = referees.get(request.refereeId());
        if (referee == null) {
            throw new RefereeException("refereeId",
                    String.format(RefereeException.REFEREE_NOT_FOUND, request.refereeId()));
        }

        Map<String, Match> matches = matchService.getMatches();
        Match match = matches.get(matchId);
        if (match == null) {
            throw new RefereeException("matchId",
                    String.format(RefereeException.MATCH_NOT_FOUND, matchId));
        }

        if (matchRefereeIndex.containsKey(matchId)) {
            throw new RefereeException("match", RefereeException.MATCH_ALREADY_HAS_REFEREE);
        }

        referee.getAssignedMatches().add(match);
        matchRefereeIndex.put(matchId, referee.getId());

        log.info("Árbitro '{}' asignado al partido {}", referee.getFullname(), matchId);
        return toResponse(referee);
    }

    @Override
    public RefereeResponse findById(String refereeId) {
        Referee referee = referees.get(refereeId);
        if (referee == null) {
            throw new RefereeException("id",
                    String.format(RefereeException.REFEREE_NOT_FOUND, refereeId));
        }
        return toResponse(referee);
    }

    @Override
    public List<RefereeResponse> findAll() {
        return referees.values().stream().map(this::toResponse).toList();
    }

    private RefereeResponse toResponse(Referee r) {
        List<AssignedMatchDTO> assignedMatches = r.getAssignedMatches() == null ? List.of()
                : r.getAssignedMatches().stream().map(m -> new AssignedMatchDTO(
                        m.getId(),
                        m.getLocalTeam().getTeamName(),
                        m.getVisitorTeam().getTeamName(),
                        m.getDateTime(),
                        String.valueOf(m.getField())
                )).toList();

        return new RefereeResponse(r.getId(), r.getFullname(), r.getEmail(), assignedMatches);
    }
}
