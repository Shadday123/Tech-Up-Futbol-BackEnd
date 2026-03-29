package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentConfigRequest;
import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentConfigResponse;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.Controller.mapper.TournamentMapper;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.validator.TournamentValidator;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.exception.ResourceNotFoundException;
import com.techcup.techcup_futbol.repository.TournamentRepository;
import com.techcup.techcup_futbol.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TournamentServiceImpl implements TournamentService {

    private static final Logger log = LoggerFactory.getLogger(TournamentServiceImpl.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    // ── CREATE

    @Override
    @Transactional
    public TournamentResponse create(CreateTournamentRequest request) {
        log.info("Creando torneo: '{}'", request.name());

        if (request == null) {
            throw new TournamentException("request", "No puede ser null");
        }

        TournamentValidator.validate(request);

        Tournament nuevoTorneo = new Tournament();
        nuevoTorneo.setId(IdGenerator.generateId());
        nuevoTorneo.setName(request.name());
        nuevoTorneo.setStartDate(request.startDate());
        nuevoTorneo.setEndDate(request.endDate());
        nuevoTorneo.setRegistrationFee(request.registrationFee());
        nuevoTorneo.setMaxTeams(request.maxTeams());
        nuevoTorneo.setRules(request.rules());
        nuevoTorneo.setCurrentState(TournamentState.DRAFT);

        tournamentRepository.save(nuevoTorneo);
        log.info("Torneo creado — ID: {} | Estado: DRAFT | MaxEquipos: {}",
                nuevoTorneo.getId(), request.maxTeams());

        return TournamentMapper.toResponse(nuevoTorneo);
    }

    // ── UPDATE STATE

    @Override
    @Transactional
    public TournamentResponse updateStatus(String id, String nextStateName) {
        log.info("Actualizando estado del torneo ID: {} → '{}'", id, nextStateName);

        Tournament torneo = obtenerTorneo(id);

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
        tournamentRepository.save(torneo);
        log.info("Estado del torneo '{}' actualizado a {}", id, next);

        return TournamentMapper.toResponse(torneo);
    }

    // ── READ — POR ID

    @Override
    public TournamentResponse findById(String id) {
        log.info("Buscando torneo con ID: {}", id);
        return TournamentMapper.toResponse(obtenerTorneo(id));
    }

    // ── READ — TODOS

    @Override
    public List<TournamentResponse> findAll() {
        List<TournamentResponse> torneos = tournamentRepository.findAll().stream()
                .map(TournamentMapper::toResponse)
                .collect(Collectors.toList());
        log.info("Total torneos listados: {}", torneos.size());
        return torneos;
    }

    // ── CONFIG — CREATE / UPDATE

    @Override
    @Transactional
    public TournamentConfigResponse createOrUpdateConfig(String tournamentId,
                                                         CreateTournamentConfigRequest request) {
        log.info("Configurando torneo ID: {}", tournamentId);

        Tournament tournament = obtenerTorneo(tournamentId);

        if (tournament.getCurrentState() == TournamentState.IN_PROGRESS
                || tournament.getCurrentState() == TournamentState.COMPLETED) {
            throw new TournamentException("state",
                    "Solo se pueden configurar torneos en estado Borrador o Activo.");
        }

        if (request.registrationDeadline() != null
                && !request.registrationDeadline().isBefore(tournament.getStartDate())) {
            throw new TournamentException("registrationDeadline",
                    "La fecha de cierre de inscripciones debe ser estrictamente anterior "
                            + "a la fecha de inicio del torneo.");
        }

        if (tournament.getConfigId() == null) {
            tournament.setConfigId(IdGenerator.generateId());
        }

        tournament.setRules(request.rules());
        tournament.setRegistrationDeadline(request.registrationDeadline());
        tournament.setSanctions(request.sanctions());

        if (request.importantDates() != null) {
            tournament.setImportantDates(request.importantDates().stream()
                    .map(d -> d.description() + "|"
                            + (d.date() != null ? d.date().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME) : ""))
                    .toList());
        }
        if (request.matchSchedules() != null) {
            tournament.setMatchSchedules(request.matchSchedules().stream()
                    .map(s -> s.dayOfWeek() + "|" + s.startTime() + "|" + s.endTime())
                    .toList());
        }
        if (request.fields() != null) {
            tournament.setFields(request.fields().stream()
                    .map(f -> f.name() + "|" + f.location())
                    .toList());
        }

        tournamentRepository.save(tournament);
        log.info("Configuración guardada para torneo ID: {}", tournamentId);
        return TournamentMapper.toConfigResponse(tournament);
    }

    // ── CONFIG — READ

    @Override
    public TournamentConfigResponse findConfig(String tournamentId) {
        Tournament tournament = obtenerTorneo(tournamentId);

        if (!tournament.hasConfig()) {
            throw new TournamentException("config",
                    String.format(TournamentException.CONFIG_NOT_FOUND, tournamentId));
        }

        return TournamentMapper.toConfigResponse(tournament);
    }

    // ── HELPERS PRIVADOS

    private Tournament obtenerTorneo(String id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(TournamentException.TOURNAMENT_NOT_FOUND, id)));
    }
}
