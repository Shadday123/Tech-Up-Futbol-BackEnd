package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.TournamentConfigDTOs.*;
import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentConfig;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.exception.TournamentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TournamentConfigServiceImpl implements TournamentConfigService {

    private static final Logger log = LoggerFactory.getLogger(TournamentConfigServiceImpl.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final Map<String, TournamentConfig> configs = new HashMap<>();

    @Override
    public TournamentConfigResponse createOrUpdate(String tournamentId, CreateTournamentConfigRequest request) {
        log.info("Configurando torneo ID: {}", tournamentId);

        Tournament tournament = DataStore.torneos.get(tournamentId);
        if (tournament == null) {
            throw new TournamentException("id",
                    String.format(TournamentException.TOURNAMENT_NOT_FOUND, tournamentId));
        }

        if (tournament.getCurrentState() == TournamentState.IN_PROGRESS
                || tournament.getCurrentState() == TournamentState.COMPLETED) {
            throw new TournamentException("state",
                    "Solo se pueden configurar torneos en estado Borrador o Activo.");
        }

        if (request.registrationDeadline() != null
                && request.registrationDeadline().isAfter(tournament.getStartDate())) {
            throw new TournamentException("registrationDeadline",
                    "La fecha de cierre de inscripciones debe ser anterior a la fecha de inicio del torneo.");
        }

        TournamentConfig config = configs.getOrDefault(tournamentId, new TournamentConfig());
        if (config.getId() == null) {
            config.setId(UUID.randomUUID().toString());
        }
        config.setTournament(tournament);
        config.setRules(request.rules());
        config.setRegistrationDeadline(request.registrationDeadline());
        config.setSanctions(request.sanctions());

        if (request.importantDates() != null) {
            config.setImportantDates(request.importantDates().stream()
                    .map(d -> d.description() + "|" + (d.date() != null ? d.date().format(FMT) : ""))
                    .toList());
        }
        if (request.matchSchedules() != null) {
            config.setMatchSchedules(request.matchSchedules().stream()
                    .map(s -> s.dayOfWeek() + "|" + s.startTime() + "|" + s.endTime())
                    .toList());
        }
        if (request.fields() != null) {
            config.setFields(request.fields().stream()
                    .map(f -> f.name() + "|" + f.location())
                    .toList());
        }

        configs.put(tournamentId, config);
        log.info("Configuración guardada para torneo ID: {}", tournamentId);
        return toResponse(config, tournamentId);
    }

    @Override
    public TournamentConfigResponse findByTournamentId(String tournamentId) {
        TournamentConfig config = configs.get(tournamentId);
        if (config == null) {
            throw new TournamentException("id",
                    String.format(TournamentException.TOURNAMENT_NOT_FOUND, tournamentId));
        }
        return toResponse(config, tournamentId);
    }

    private TournamentConfigResponse toResponse(TournamentConfig c, String tournamentId) {
        List<ImportantDateDTO> dates = c.getImportantDates() == null ? List.of()
                : c.getImportantDates().stream().map(s -> {
                    String[] parts = s.split("\\|", 2);
                    return new ImportantDateDTO(parts[0],
                            parts.length > 1 && !parts[1].isBlank()
                                    ? java.time.LocalDateTime.parse(parts[1], FMT) : null);
                }).toList();

        List<MatchScheduleDTO> schedules = c.getMatchSchedules() == null ? List.of()
                : c.getMatchSchedules().stream().map(s -> {
                    String[] p = s.split("\\|", 3);
                    return new MatchScheduleDTO(p[0], p.length > 1 ? p[1] : "", p.length > 2 ? p[2] : "");
                }).toList();

        List<FieldDTO> fields = c.getFields() == null ? List.of()
                : c.getFields().stream().map(s -> {
                    String[] p = s.split("\\|", 2);
                    return new FieldDTO(p[0], p.length > 1 ? p[1] : "");
                }).toList();

        return new TournamentConfigResponse(
                c.getId(), tournamentId,
                c.getRules(), c.getRegistrationDeadline(),
                dates, schedules, fields, c.getSanctions()
        );
    }
}
