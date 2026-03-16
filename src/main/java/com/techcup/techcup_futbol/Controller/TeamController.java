package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.CreateTeamRequest;
import com.techcup.techcup_futbol.Controller.dto.TeamResponse;
import com.techcup.techcup_futbol.core.service.TeamService;
import com.techcup.techcup_futbol.core.model.Team;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody CreateTeamRequest request) {
        Team teamEntity = new Team();
        teamEntity.setTeamName(request.getTeamName());
        teamEntity.setShieldUrl(request.getShieldUrl());
        teamEntity.setUniformColors(request.getUniformColors());
        Team savedTeam = teamService.createTeam(teamEntity);

        TeamResponse response = new TeamResponse(
                savedTeam.getId(),
                savedTeam.getTeamName(),
                savedTeam.getShieldUrl(),
                savedTeam.getUniformColors(),
                savedTeam.getCaptain() != null ? savedTeam.getCaptain().getFullname() : null,
                savedTeam.getPlayers() != null ?
                        savedTeam.getPlayers().stream()
                                .map(p -> p.getId())
                                .collect(Collectors.toList()) :
                        List.of()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> findAll() {
        List<Team> teams = teamService.getAllTeams();

        List<TeamResponse> response = teams.stream()
                .map(t -> new TeamResponse(
                        t.getId(),
                        t.getTeamName(),
                        t.getShieldUrl(),
                        t.getUniformColors(),
                        t.getCaptain() != null ? t.getCaptain().getFullname() : null,
                        t.getPlayers() != null ?
                                t.getPlayers().stream()
                                        .map(p -> p.getId())
                                        .collect(Collectors.toList()) :
                                List.of()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> findById(@PathVariable String id) {
        Team team = teamService.getTeamById(id);
        if (team == null) return ResponseEntity.notFound().build();

        TeamResponse response = new TeamResponse(
                team.getId(),
                team.getTeamName(),
                team.getShieldUrl(),
                team.getUniformColors(),
                team.getCaptain() != null ? team.getCaptain().getFullname() : null,
                team.getPlayers() != null ?
                        team.getPlayers().stream()
                                .map(p -> p.getId())
                                .collect(Collectors.toList()) :
                        List.of()
        );
        return ResponseEntity.ok(response);
    }
}