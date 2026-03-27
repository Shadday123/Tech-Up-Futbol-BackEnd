package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.CreateTournamentRequest;
import com.techcup.techcup_futbol.Controller.dto.TournamentResponse;
import com.techcup.techcup_futbol.Controller.dto.TournamentConfigDTOs.*;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.repository.TournamentRepository;
import com.techcup.techcup_futbol.core.validator.TournamentValidator;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class TournamentServiceImpl implements TournamentService {

    private static final Logger log = LoggerFactory.getLogger(TournamentServiceImpl.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final TournamentRepository tournamentRepository;

    public TournamentServiceImpl(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    // ── CREATE

    @Override
    public TournamentResponse create(CreateTournamentRequest request) {
        log.info("Creando torneo: '{}'", request.name());

        TournamentValidator.validate(request);

        Tournament torneo = new Tournament();
        torneo.setName(request.name());
        torneo.setStartDate(request.startDate());
        torneo.setEndDate(request.endDate());
        torneo.setRegistrationFee(request.registrationFee());
        torneo.setMaxTeams(request.maxTeams());
        torneo.setRules(request.rules());
        torneo.setCurrentState(TournamentState.DRAFT);

        Tournament guardado = tournamentRepository.save(torneo);

        log.info("Torneo creado — ID: {} | Estado: DRAFT", guardado.getId());

        return mapToResponse(guardado);
    }

    // ── UPDATE STATE

    @Override
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

        log.info("Estado actualizado a {}", next);

        return mapToResponse(torneo);
    }

    // ── READ — POR ID

    @Override
    public TournamentResponse findById(String id) {
        log.info("Buscando torneo con ID: {}", id);

        Tournament torneo = obtenerTorneo(id);

        return mapToResponse(torneo);
    }

    // ── READ — TODOS

    @Override
    public List<TournamentResponse> findAll() {
        List<TournamentResponse> lista = tournamentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        log.info("Total torneos: {}", lista.size());

        return lista;
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
                    "Solo se pueden configurar torneos en estado DRAFT o ACTIVE.");
        }

        if (request.registrationDeadline() != null
                && !request.registrationDeadline().isBefore(tournament.getStartDate())) {
            throw new TournamentException("registrationDeadline",
                    "La fecha de cierre debe ser anterior al inicio.");
        }

        if (tournament.getConfigId() == null) {
            tournament.setConfigId(UUID.randomUUID().toString());
        }

        tournament.setRules(request.rules());
        tournament.setRegistrationDeadline(request.registrationDeadline());
        tournament.setSanctions(request.sanctions());

        if (request.importantDates() != null) {
            tournament.setImportantDates(request.importantDates().stream()
                    .map(d -> d.description() + "|" +
                            (d.date() != null ? d.date().format(FMT) : ""))
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

        log.info("Configuración guardada");

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

    // ── HELPERS

    private Tournament obtenerTorneo(String id) {
        return tournamentRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new TournamentException("id",
                        String.format(TournamentException.TOURNAMENT_NOT_FOUND, id)));
    }

    private TournamentResponse mapToResponse(Tournament t) {
        return new TournamentResponse(
                t.getId().toString(),
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
            return new ImportantDateDTO(
                    parts[0],
                    parts.length > 1 && !parts[1].isBlank()
                            ? LocalDateTime.parse(parts[1], FMT)
                            : null
            );
        }).toList();

        List<MatchScheduleDTO> schedules = t.getMatchSchedules() == null ? List.of()
                : t.getMatchSchedules().stream().map(s -> {
            String[] p = s.split("\\|", 3);
            return new MatchScheduleDTO(
                    p[0],
                    p.length > 1 ? p[1] : "",
                    p.length > 2 ? p[2] : ""
            );
        }).toList();

        List<FieldDTO> fields = t.getFields() == null ? List.of()
                : t.getFields().stream().map(s -> {
            String[] p = s.split("\\|", 2);
            return new FieldDTO(
                    p[0],
                    p.length > 1 ? p[1] : ""
            );
        }).toList();

        return new TournamentConfigResponse(
                t.getConfigId(),
                t.getId().toString(),
                t.getRules(),
                t.getRegistrationDeadline(),
                dates,
                schedules,
                fields,
                t.getSanctions()
        );
    }
}