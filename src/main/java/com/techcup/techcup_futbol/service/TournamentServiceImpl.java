package com.techcup.techcup_futbol.service;

import com.techcup.techcup_futbol.model.Tournament;
import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.model.TournamentState;
import com.techcup.techcup_futbol.validator.TournamentValidator;
import com.techcup.techcup_futbol.model.DataStore;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TournamentServiceImpl implements TournamentService {

    @Override
    public TournamentResponse create(CreateTournamentRequest request) {
        //valida
        TournamentValidator.validate(request);

        // crea el torneo
        Tournament nuevoTorneo = new Tournament();
        String id = "T" + String.format("%03d", DataStore.torneos.size() + 1); // Genera T004, T005...

        nuevoTorneo.setId(id);
        nuevoTorneo.setName(request.name());
        nuevoTorneo.setStartDate(request.startDate());
        nuevoTorneo.setEndDate(request.endDate());
        nuevoTorneo.setRegistrationFee(request.registrationFee());
        nuevoTorneo.setMaxTeams(request.maxTeams());
        nuevoTorneo.setRules(request.rules());
        nuevoTorneo.setCurrentState(TournamentState.DRAFT);

        //  Guardar en tu DataStore
        DataStore.torneos.put(id, nuevoTorneo);

        return mapToResponse(nuevoTorneo);
    }

    @Override
    public TournamentResponse updateStatus(String id, String nextStateName) {
        Tournament torneo = DataStore.torneos.get(id);
        if (torneo == null) {
            throw new NoSuchElementException("No se encontró el torneo con ID: " + id);
        }

        TournamentState next = TournamentState.valueOf(nextStateName.toUpperCase());

        // Validar transición
        TournamentValidator.validateStateTransition(torneo.getCurrentState(), next);

        torneo.setCurrentState(next);
        return mapToResponse(torneo);
    }

    @Override
    public TournamentResponse findById(String id) {
        Tournament torneo = DataStore.torneos.get(id);
        if (torneo == null) {
            throw new NoSuchElementException("Torneo no encontrado");
        }
        return mapToResponse(torneo);
    }

    @Override
    public List<TournamentResponse> findAll() {
        return DataStore.torneos.values().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Método helper para convertir Entidad a DTO de respuesta
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
