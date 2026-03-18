package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.CreateTeamRequest;
import com.techcup.techcup_futbol.Controller.dto.TeamResponse;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.service.PlayerService;
import com.techcup.techcup_futbol.core.service.TeamService;
import com.techcup.techcup_futbol.exception.TeamException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private static final Logger log = LoggerFactory.getLogger(TeamController.class);

    private final TeamService teamService;
    private final PlayerService playerService;

    public TeamController(TeamService teamService, PlayerService playerService) {
        this.teamService = teamService;
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody CreateTeamRequest request) {
        log.info("POST /api/teams — nombre: {}", request.getTeamName());

        Team teamEntity = new Team();
        teamEntity.setTeamName(request.getTeamName());
        teamEntity.setShieldUrl(request.getShieldUrl());
        teamEntity.setUniformColors(request.getUniformColors());

        if (request.getCaptainId() != null) {
            Player capitan = playerService.obtenerPorId(request.getCaptainId());
            teamEntity.setCaptain(capitan);
        }

        Team saved = teamService.createTeam(teamEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> findAll() {
        log.info("GET /api/teams");
        List<TeamResponse> response = teamService.getAllTeams().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> findById(@PathVariable String id) {
        log.info("GET /api/teams/{}", id);
        return teamService.buscarPorId(id)
                .map(t -> ResponseEntity.ok(toResponse(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("DELETE /api/teams/{}", id);
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<Void> invitePlayer(
            @PathVariable String teamId,
            @PathVariable String playerId) {
        log.info("POST /api/teams/{}/players/{}", teamId, playerId);
        Player player = playerService.obtenerPorId(playerId);
        teamService.invitePlayer(teamId, player);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<Void> removePlayer(
            @PathVariable String teamId,
            @PathVariable String playerId) {
        log.info("DELETE /api/teams/{}/players/{}", teamId, playerId);
        teamService.removePlayer(teamId, playerId);
        return ResponseEntity.noContent().build();
    }

    private TeamResponse toResponse(Team t) {
        return new TeamResponse(
                t.getId(),
                t.getTeamName(),
                t.getShieldUrl(),
                t.getUniformColors(),
                t.getCaptain() != null ? t.getCaptain().getFullname() : null,
                t.getPlayers() != null
                        ? t.getPlayers().stream().map(Player::getId).collect(Collectors.toList())
                        : List.of()
        );
    }

    @ExceptionHandler(TeamException.class)
    public ResponseEntity<String> handleTeamException(TeamException e) {
        log.error("TeamException — campo: {} | mensaje: {}", e.getField(), e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}