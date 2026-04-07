package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.validator.TournamentValidator;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.core.exception.ResourceNotFoundException;
import com.techcup.techcup_futbol.persistence.entity.TournamentEntity;
import com.techcup.techcup_futbol.persistence.mapper.TournamentPersistenceMapper;
import com.techcup.techcup_futbol.persistence.repository.TournamentRepository;
import com.techcup.techcup_futbol.core.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public Tournament create(Tournament tournament) {
        log.info("Creando torneo: '{}'", tournament.getName());

        if (tournament == null) {
            throw new TournamentException("request", "No puede ser null");
        }

        TournamentValidator.validate(tournament);

        tournament.setId(IdGenerator.generateId());
        tournament.setCurrentState(TournamentState.DRAFT);

        TournamentEntity entity = TournamentPersistenceMapper.toEntity(tournament);
        tournamentRepository.save(entity);

        log.info("Torneo creado — ID: {} | Estado: DRAFT | MaxEquipos: {}",
                tournament.getId(), tournament.getMaxTeams());

        return tournament;
    }

    // ── UPDATE STATE

    @Override
    @Transactional
    public Tournament updateStatus(String id, String nextStateName) {
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
        TournamentEntity entity = TournamentPersistenceMapper.toEntity(torneo);
        tournamentRepository.save(entity);
        log.info("Estado del torneo '{}' actualizado a {}", id, next);

        return torneo;
    }

    // ── READ — POR ID

    @Override
    public Tournament findById(String id) {
        log.info("Buscando torneo con ID: {}", id);
        return obtenerTorneo(id);
    }

    // ── READ — TODOS

    @Override
    public List<Tournament> findAll() {
        List<Tournament> torneos = tournamentRepository.findAll()
                .stream()
                .map(TournamentPersistenceMapper::toDomain)
                .collect(Collectors.toList());
        log.info("Total torneos listados: {}", torneos.size());
        return torneos;
    }

    // ── CONFIG — CREATE / UPDATE

    @Override
    @Transactional
    public Tournament createOrUpdateConfig(String tournamentId, String rules,
                                           LocalDateTime registrationDeadline,
                                           List<String> importantDates,
                                           List<String> matchSchedules,
                                           List<String> fields, String sanctions) {
        log.info("Configurando torneo ID: {}", tournamentId);

        Tournament tournament = obtenerTorneo(tournamentId);

        if (tournament.getCurrentState() == TournamentState.IN_PROGRESS
                || tournament.getCurrentState() == TournamentState.COMPLETED) {
            throw new TournamentException("state",
                    "Solo se pueden configurar torneos en estado Borrador o Activo.");
        }

        if (registrationDeadline != null
                && !registrationDeadline.isBefore(tournament.getStartDate())) {
            throw new TournamentException("registrationDeadline",
                    "La fecha de cierre de inscripciones debe ser estrictamente anterior "
                            + "a la fecha de inicio del torneo.");
        }

        if (tournament.getConfigId() == null) {
            tournament.setConfigId(IdGenerator.generateId());
        }

        tournament.setRules(rules);
        tournament.setRegistrationDeadline(registrationDeadline);
        tournament.setSanctions(sanctions);
        tournament.setImportantDates(importantDates);
        tournament.setMatchSchedules(matchSchedules);
        tournament.setFields(fields);

        TournamentEntity entity = TournamentPersistenceMapper.toEntity(tournament);
        tournamentRepository.save(entity);
        log.info("Configuración guardada para torneo ID: {}", tournamentId);
        return tournament;
    }

    // ── CONFIG — READ

    @Override
    public Tournament findConfig(String tournamentId) {
        Tournament tournament = obtenerTorneo(tournamentId);

        if (!tournament.hasConfig()) {
            throw new TournamentException("config",
                    String.format(TournamentException.CONFIG_NOT_FOUND, tournamentId));
        }

        return tournament;
    }

    // ── HELPERS PRIVADOS

    private Tournament obtenerTorneo(String id) {
        return tournamentRepository.findById(id)
                .map(TournamentPersistenceMapper::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(TournamentException.TOURNAMENT_NOT_FOUND, id)));
    }
}

