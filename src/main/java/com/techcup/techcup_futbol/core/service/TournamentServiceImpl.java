package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.Controller.dto.TournamentConfigDTOs.*;
import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.validator.TournamentValidator;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.techcup.techcup_futbol.util.IdGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TournamentServiceImpl implements TournamentService {

    private static final Logger log = LoggerFactory.getLogger(TournamentServiceImpl.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // ── CREATE

    @Override
    public TournamentResponse create(CreateTournamentRequest request) {
        log.info("Creando torneo: '{}'", request.name());

        TournamentValidator.validate(request);

        String id = IdGenerator.generateId();

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

    // ── CONFIG — CREATE / UPDATE

    @Override
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
                            + (d.date() != null ? d.date().format(FMT) : ""))
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

        log.info("Configuración guardada para torneo ID: {}", tournamentId);
        return toConfigResponse(tournament);
    }

    // ── CONFIG — READ

    @Override
    public TournamentConfigResponse findConfig(String tournamentId) {
        Tournament tournament = obtenerTorneo(tournamentId);

        if (!tournament.hasConfig()) {
            throw new TournamentException("config",
                    String.format(TournamentException.CONFIG_NOT_FOUND, tournamentId));
        }

        return toConfigResponse(tournament);
    }

    // ── HELPERS PRIVADOS

    private Tournament obtenerTorneo(String id) {
        Tournament t = DataStore.torneos.get(id);
        if (t == null) {
            throw new TournamentException("id",
                    String.format(TournamentException.TOURNAMENT_NOT_FOUND, id));
        }
        return t;
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

    private TournamentConfigResponse toConfigResponse(Tournament t) {
        List<ImportantDateDTO> dates = t.getImportantDates() == null ? List.of()
                : t.getImportantDates().stream().map(s -> {
            String[] parts = s.split("\\|", 2);
            return new ImportantDateDTO(parts[0],
                    parts.length > 1 && !parts[1].isBlank()
                            ? LocalDateTime.parse(parts[1], FMT) : null);
        }).toList();

        List<MatchScheduleDTO> schedules = t.getMatchSchedules() == null ? List.of()
                : t.getMatchSchedules().stream().map(s -> {
            String[] p = s.split("\\|", 3);
            return new MatchScheduleDTO(p[0],
                    p.length > 1 ? p[1] : "",
                    p.length > 2 ? p[2] : "");
        }).toList();

        List<FieldDTO> fields = t.getFields() == null ? List.of()
                : t.getFields().stream().map(s -> {
            String[] p = s.split("\\|", 2);
            return new FieldDTO(p[0], p.length > 1 ? p[1] : "");
        }).toList();

        return new TournamentConfigResponse(
                t.getConfigId(), t.getId(),
                t.getRules(), t.getRegistrationDeadline(),
                dates, schedules, fields, t.getSanctions()
        );
    }
}
