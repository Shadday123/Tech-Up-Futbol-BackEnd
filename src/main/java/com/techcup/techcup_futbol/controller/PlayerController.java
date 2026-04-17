package com.techcup.techcup_futbol.controller;

import com.techcup.techcup_futbol.controller.dto.PlayerDTO;
import com.techcup.techcup_futbol.controller.dto.PlayerResponse;
import com.techcup.techcup_futbol.controller.dto.PlayerStatsResponse;
import com.techcup.techcup_futbol.controller.mapper.PlayerMapper;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.security.JwtUtil;
import com.techcup.techcup_futbol.core.service.PlayerService;
import com.techcup.techcup_futbol.core.service.StandingsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/players")
@Tag(name = "Jugadores", description = "Registro de jugadores, actualización de perfil y gestión de disponibilidad para el mercado de fichajes")
public class PlayerController {

    private static final Logger log = LoggerFactory.getLogger(PlayerController.class);

    private final PlayerService playerService;
    private final StandingsService standingsService;
    private final JwtUtil jwtUtil;

    public PlayerController(PlayerService playerService, StandingsService standingsService, JwtUtil jwtUtil) {
        this.playerService = playerService;
        this.standingsService = standingsService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/registro")
    public ResponseEntity<PlayerResponse> registrar(@Valid @RequestBody PlayerDTO dto) {
        log.info("POST /api/players/registro — jugador: {} | email: {} | tipo: {}",
                dto.getFullname(), dto.getEmail(), dto.getPlayerType());

        Player jugador = PlayerMapper.toModel(dto);
        playerService.registrar(jugador, dto.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PlayerMapper.mapToResponse(jugador));
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<PlayerResponse> actualizarPerfil(
            @PathVariable String id,
            @RequestBody java.util.Map<String, String> body) {

        log.info("PUT /api/players/{}/perfil", id);
        String foto = body.get("photoUrl");
        Player jugador = playerService.obtenerPorId(id);
        playerService.actualizarPerfil(jugador, foto);
        return ResponseEntity.ok(PlayerMapper.mapToResponse(jugador));
    }

    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<PlayerResponse> cambiarDisponibilidad(
            @PathVariable String id,
            @RequestParam boolean disponible) {

        log.info("PATCH /api/players/{}/disponibilidad — disponible: {}", id, disponible);
        Player jugador = playerService.obtenerPorId(id);
        playerService.cambiarDisponibilidad(jugador, disponible);
        return ResponseEntity.ok(PlayerMapper.mapToResponse(jugador));
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> listar() {
        log.info("GET /api/players");
        List<PlayerResponse> respuesta = playerService.listarJugadores()
                .stream()
                .map(PlayerMapper::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> buscarPorId(@PathVariable String id) {
        log.info("GET /api/players/{}", id);
        return playerService.buscarPorId(id)
                .map(p -> ResponseEntity.ok(PlayerMapper.mapToResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<PlayerStatsResponse> obtenerStats(@PathVariable String id) {
        log.info("GET /api/players/{}/stats", id);
        int matchesPlayed = standingsService.findByPlayerId(id).stream()
                .mapToInt(s -> s.getMatchesPlayed())
                .findFirst()
                .orElse(0);
        return ResponseEntity.ok(new PlayerStatsResponse(matchesPlayed, 0, 0, 0));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        log.info("DELETE /api/players/{}", id);
        playerService.eliminarJugador(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/capitan")
    public ResponseEntity<CapitanResponse> convertirCapitan(@PathVariable String id, Authentication auth) {
        log.info("PATCH /api/players/{}/capitan", id);
        Player jugador = playerService.obtenerPorId(id);
        if (!jugador.getEmail().equals(auth.getName())) {
            throw new AccessDeniedException("Solo puedes convertirte en capitán a ti mismo");
        }
        playerService.convertirCapitan(id);
        String newToken = jwtUtil.generateToken(jugador.getEmail(), "ROLE_CAPITAN");
        return ResponseEntity.ok(new CapitanResponse(newToken, jugador.getEmail(), "ROLE_CAPITAN", "Ahora eres capitán", id));
    }

    public record CapitanResponse(String token, String email, String rol, String mensaje, String userId) {}

}