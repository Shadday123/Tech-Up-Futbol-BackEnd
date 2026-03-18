package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.validator.TournamentValidator;
import com.techcup.techcup_futbol.exception.TournamentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TournamentServiceImpl implements TournamentService {

    private static final Logger log = LoggerFactory.getLogger(TournamentServiceImpl.class);

    // ── CREATE

    @Override
    public TournamentResponse create(CreateTournamentRequest request) {
        log.info("Creando torneo: '{}'", request.name());

        TournamentValidator.validate(request);

        int nextNum = DataStore.torneos.keySet().stream()
                .filter(k -> k.matches("T\\d+"))
                .mapToInt(k -> Integer.parseInt(k.substring(1)))
                .max()
                .orElse(0) + 1;
        String id = "T" + String.format("%03d", nextNum);

        Tournament nuevoTorneo = new Tournament();
        nuevoTorneo.setId(id);
        nuevoTorneo.setName(request.name());
        nuevoTorneo.setStartDate(request.startDate());
        nuevoTorneo.setEndDate(request.endDate());
        nuevoTorneo.setRegistrationFee(request.registrationFee());
        nuevoTorneo.setMaxTeams(request.maxTeams());
        nuevoTorneo.setRules(request.rules());
        nuevoTorneo.setCurrentState(TournamentState.DRAFT);

        DataStore.torneos.put(id, nuevoTorneo);
        log.info("Torneo creado — ID: {} | Estado: DRAFT | MaxEquipos: {}",
                id, request.maxTeams());

        return mapToResponse(nuevoTorneo);
    }

    // ── UPDATE STATE

    @Override
    public TournamentResponse updateStatus(String id, String nextStateName) {
        log.info("Actualizando estado del torneo ID: {} → '{}'", id, nextStateName);

        Tournament torneo = DataStore.torneos.get(id);
        if (torneo == null) {
            throw new TournamentException("id",
                    String.format(TournamentException.TOURNAMENT_NOT_FOUND, id));
        }

        TournamentState next;
        try {
            next = TournamentState.valueOf(nextStateName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TournamentException("state",
                    String.format(TournamentException.INVALID_STATE_NAME,
                            nextStateName, Arrays.toString(TournamentState.values())));
        }

        TournamentValidator.validateStateTransition(torneo.getCurrentState(), next);

        torneo.setCurrentState(next);
        log.info("Estado del torneo '{}' actualizado a {}", id, next);

        return mapToResponse(torneo);
    }

    // ── READ — POR ID 

    @Override
    public TournamentResponse findById(String id) {
        log.info("Buscando torneo con ID: {}", id);

        Tournament torneo = DataStore.torneos.get(id);
        if (torneo == null) {
            throw new TournamentException("id",
                    String.format(TournamentException.TOURNAMENT_NOT_FOUND, id));
        }

        return mapToResponse(torneo);
    }

    // ── READ — TODOS

    @Override
    public List<TournamentResponse> findAll() {
        List<TournamentResponse> torneos = DataStore.torneos.values().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        log.info("Total torneos listados: {}", torneos.size());
        return torneos;
    }

    // ── HELPER

    private TournamentResponse mapToResponse(Tournament t) {
        return new TournamentResponse(
                t.getId(),
                t.getName(),
                t.getStartDate(),
                t.getEndDate(),
                t.getRegistrationFee(),
                t.getMaxTeams(),
                t.getRules(),
                t.getCurrentState().name()
        );
    }
}