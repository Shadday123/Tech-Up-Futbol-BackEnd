package com.techcup.techcup_futbol.service;

import com.techcup.techcup_futbol.model.Tournament;
import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.model.TournamentState;
import com.techcup.techcup_futbol.validator.TournamentValidator;
import com.techcup.techcup_futbol.model.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TournamentServiceImpl implements TournamentService {

    private static final Logger log = LoggerFactory.getLogger(TournamentServiceImpl.class);

    @Override
    public TournamentResponse create(CreateTournamentRequest request) {
        log.info("Creando torneo con nombre: {}", request.name());

        TournamentValidator.validate(request);

        Tournament nuevoTorneo = new Tournament();
        String id = "T" + String.format("%03d", DataStore.torneos.size() + 1);

        nuevoTorneo.setId(id);
        nuevoTorneo.setName(request.name());
        nuevoTorneo.setStartDate(request.startDate());
        nuevoTorneo.setEndDate(request.endDate());
        nuevoTorneo.setRegistrationFee(request.registrationFee());
        nuevoTorneo.setMaxTeams(request.maxTeams());
        nuevoTorneo.setRules(request.rules());
        nuevoTorneo.setCurrentState(TournamentState.DRAFT);

        DataStore.torneos.put(id, nuevoTorneo);
        log.info("Torneo creado exitosamente con id: {}", id);

        return mapToResponse(nuevoTorneo);
    }

    @Override
    public TournamentResponse updateStatus(String id, String nextStateName) {
        log.info("Actualizando estado del torneo id: {} a {}", id, nextStateName);

        Tournament torneo = DataStore.torneos.get(id);
        if (torneo == null) {
            log.warn("No se encontró el torneo con id: {}", id);
            throw new NoSuchElementException("No se encontró el torneo con ID: " + id);
        }

        TournamentState next = TournamentState.valueOf(nextStateName.toUpperCase());
        TournamentValidator.validateStateTransition(torneo.getCurrentState(), next);

        torneo.setCurrentState(next);
        log.info("Estado del torneo id: {} actualizado a {}", id, next);

        return mapToResponse(torneo);
    }

    @Override
    public TournamentResponse findById(String id) {
        log.info("Buscando torneo con id: {}", id);

        Tournament torneo = DataStore.torneos.get(id);
        if (torneo == null) {
            log.warn("No se encontró torneo con id: {}", id);
            throw new NoSuchElementException("Torneo no encontrado");
        }

        return mapToResponse(torneo);
    }

    @Override
    public List<TournamentResponse> findAll() {
        List<TournamentResponse> torneos = DataStore.torneos.values().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        log.info("Listando torneos — total: {}", torneos.size());
        return torneos;
    }

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