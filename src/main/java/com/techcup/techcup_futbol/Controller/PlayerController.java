package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.service.PlayerService;
import com.techcup.techcup_futbol.exception.PlayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private static final Logger log = LoggerFactory.getLogger(PlayerController.class);

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@RequestBody Player jugador, @RequestParam String correo) {
        log.info("POST /api/players/registro — jugador: {} | email: {}", jugador.getFullname(), correo);
        playerService.registrar(jugador, correo);
        return ResponseEntity.status(HttpStatus.CREATED).body("Jugador registrado exitosamente");
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<Void> actualizarPerfil(@PathVariable String id, @RequestBody String foto) {
        log.info("PUT /api/players/{}/perfil", id);
        Player jugador = playerService.obtenerPorId(id);
        playerService.actualizarPerfil(jugador, foto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/disponibilidad")
    public ResponseEntity<Void> cambiarDisponibilidad(
            @PathVariable String id,
            @RequestParam boolean disponible) {
        log.info("PUT /api/players/{}/disponibilidad — disponible: {}", id, disponible);
        Player jugador = playerService.obtenerPorId(id);
        playerService.cambiarDisponibilidad(jugador, disponible);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Player>> listar() {
        log.info("GET /api/players");
        return ResponseEntity.ok(playerService.listarJugadores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> buscarPorId(@PathVariable String id) {
        log.info("GET /api/players/{}", id);
        Optional<Player> resultado = playerService.buscarPorId(id);
        return resultado.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        log.info("DELETE /api/players/{}", id);
        playerService.eliminarJugador(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(PlayerException.class)
    public ResponseEntity<String> handlePlayerException(PlayerException e) {
        log.error("PlayerException — campo: {} | mensaje: {}", e.getField(), e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}