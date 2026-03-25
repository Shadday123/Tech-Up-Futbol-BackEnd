package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.Controller.dto.PlayerSearchRequest;
import com.techcup.techcup_futbol.Controller.dto.PlayerSearchResult;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.service.PlayerSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players/search")
@Tag(name = "Mercado de Jugadores", description = "Búsqueda y filtrado de jugadores disponibles (sin equipo y con disponibilidad activa) para ser invitados a equipos")
public class PlayerSearchController {

    private static final Logger log = LoggerFactory.getLogger(PlayerSearchController.class);

    private final PlayerSearchService playerSearchService;

    public PlayerSearchController(PlayerSearchService playerSearchService) {
        this.playerSearchService = playerSearchService;
    }

    @GetMapping
    public ResponseEntity<List<PlayerSearchResult>> search(
            @RequestParam(required = false) PositionEnum position,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer numberID) {

        log.info("GET /api/players/search — filtros: position={} semester={} age={}-{} gender={} name={} id={}",
                position, semester, minAge, maxAge, gender, name, numberID);

        PlayerSearchRequest filters = new PlayerSearchRequest(
                position, semester, minAge, maxAge, gender, name, numberID);

        List<PlayerSearchResult> results = playerSearchService.search(filters);
        return ResponseEntity.ok(results);
    }
}
