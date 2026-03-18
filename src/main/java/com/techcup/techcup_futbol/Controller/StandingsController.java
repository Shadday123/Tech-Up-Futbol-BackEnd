package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.StandingsDTOs.StandingsResponse;
import com.techcup.techcup_futbol.core.service.StandingsService;
import com.techcup.techcup_futbol.core.service.StandingsServiceImpl;
import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.exception.TournamentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/standings")
public class StandingsController {

    private static final Logger log = LoggerFactory.getLogger(StandingsController.class);

    private final StandingsService standingsService;
    private final StandingsServiceImpl standingsServiceImpl;

    public StandingsController(StandingsService standingsService,
                               StandingsServiceImpl standingsServiceImpl) {
        this.standingsService = standingsService;
        this.standingsServiceImpl = standingsServiceImpl;
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<StandingsResponse> findByTournament(@PathVariable String tournamentId) {
        log.info("GET /api/standings/tournament/{}", tournamentId);
        return ResponseEntity.ok(standingsService.findByTournamentId(tournamentId));
    }

    @PostMapping("/tournament/{tournamentId}/register-team/{teamId}")
    public ResponseEntity<String> registerTeam(
            @PathVariable String tournamentId,
            @PathVariable String teamId) {
        log.info("POST /api/standings/tournament/{}/register-team/{}", tournamentId, teamId);

        Team team = DataStore.equipos.get(teamId);
        if (team == null) {
            return ResponseEntity.badRequest()
                    .body("No existe equipo con ID: " + teamId);
        }
        standingsServiceImpl.registerTeamInTournament(tournamentId, team);
        return ResponseEntity.ok("Equipo '" + team.getTeamName() + "' registrado en tabla de posiciones.");
    }

    @ExceptionHandler(TournamentException.class)
    public ResponseEntity<String> handleTournamentException(TournamentException e) {
        log.error("TournamentException — {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
