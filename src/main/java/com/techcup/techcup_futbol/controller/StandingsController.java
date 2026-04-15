package com.techcup.techcup_futbol.controller;

import com.techcup.techcup_futbol.controller.dto.StandingsResponse;
import com.techcup.techcup_futbol.controller.mapper.StandingsMapper;
import com.techcup.techcup_futbol.controller.mapper.TeamMapper;
import com.techcup.techcup_futbol.core.model.Standings;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.core.service.StandingsService;
import com.techcup.techcup_futbol.core.service.TournamentService;
import com.techcup.techcup_futbol.persistence.repository.TeamRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/standings")
@Tag(name = "Tabla de Posiciones", description = "Inscripción de equipos en torneos y consulta de la tabla de posiciones. Puntuación: Victoria = 3 pts, Empate = 1 pt, Derrota = 0 pts")
public class StandingsController {

    private static final Logger log = LoggerFactory.getLogger(StandingsController.class);

    private final StandingsService standingsService;
    private final TournamentService tournamentService;
    private final TeamRepository teamRepository;

    public StandingsController(StandingsService standingsService, TournamentService tournamentService, TeamRepository teamRepository) {
        this.standingsService = standingsService;
        this.tournamentService = tournamentService;
        this.teamRepository = teamRepository;
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<StandingsResponse> findByTournament(
            @PathVariable String tournamentId) {
        log.info("GET /api/standings/tournament/{}", tournamentId);
        Tournament tournament = tournamentService.findById(tournamentId);
        List<Standings> sorted = standingsService.findByTournamentId(tournamentId);
        return ResponseEntity.ok(StandingsMapper.toResponse(tournamentId, tournament.getName(), sorted));
    }

    @PostMapping("/tournament/{tournamentId}/register-team/{teamId}")
    public ResponseEntity<String> registerTeam(
            @PathVariable String tournamentId,
            @PathVariable String teamId) {
        log.info("POST /api/standings/tournament/{}/register-team/{}", tournamentId, teamId);

        teamRepository.findById(teamId).ifPresentOrElse(
                teamEntity -> {
                    Team team = TeamMapper.toModel(teamEntity);
                    standingsService.registerTeamInTournament(tournamentId, team);
                },
                () -> log.warn("Equipo no encontrado: {}", teamId)
        );

        return ResponseEntity.ok("Equipo registrado correctamente o ya existe en el torneo.");
    }
}
