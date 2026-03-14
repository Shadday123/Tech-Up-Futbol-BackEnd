package com.techcup.techcup_futbol.Controller;


import com.techcup.techcup_futbol.Controller.dto.CreateTeamRequest;
import com.techcup.techcup_futbol.Controller.dto.TeamResponse;
import com.techcup.techcup_futbol.service.TeamService;
import com.techcup.techcup_futbol.model.Team;
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
        teamEntity.setName(request.name());

        Team savedTeam = teamService.createTeam(teamEntity);

        TeamResponse response = new TeamResponse(
                savedTeam.getId(),
                savedTeam.getName(),
                null, "PENDING", null, null, 0
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> findAll() {
        List<Team> teams = teamService.getAllTeams();

        List<TeamResponse> response = teams.stream()
                .map(t -> new TeamResponse(t.getId(), t.getName(), null, "ACTIVE", null, null, 0))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> findById(@PathVariable String id) {
        Team team = teamService.getTeamById(id);
        if (team == null) return ResponseEntity.notFound().build();

        TeamResponse response = new TeamResponse(team.getId(), team.getName(), null, "ACTIVE", null, null, 0);
        return ResponseEntity.ok(response);
    }
}
