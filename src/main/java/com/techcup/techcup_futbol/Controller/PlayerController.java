package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.service.PlayerService;
import com.techcup.techcup_futbol.exception.PlayerException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/players")
@Tag(name = "Players", description = "API para la gestión de jugadores")
public class PlayerController {

    private static final Logger log = LoggerFactory.getLogger(PlayerController.class);

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Operation(summary = "Registrar un jugador", description = "Crea un nuevo jugador en el sistema")
    @ApiResponse(responseCode = "201", description = "Jugador registrado correctamente")
    @PostMapping("/registro")
    public ResponseEntity<String> registrar(
            @RequestBody Player jugador,
            @Parameter(description = "Correo del jugador") @RequestParam String correo) {

        log.info("POST /api/players/registro — jugador: {} | email: {}", jugador.getFullname(), correo);
        playerService.registrar(jugador, correo);
        return ResponseEntity.status(HttpStatus.CREATED).body("Jugador registrado exitosamente");
    }

    @Operation(summary = "Actualizar perfil", description = "Actualiza la foto de perfil del jugador")
    @ApiResponse(responseCode = "200", description = "Perfil actualizado correctamente")
    @PutMapping("/{id}/perfil")
    public ResponseEntity<Void> actualizarPerfil(
            @Parameter(description = "ID del jugador") @PathVariable String id,
            @RequestBody String foto) {

        log.info("PUT /api/players/{}/perfil", id);
        Player jugador = playerService.obtenerPorId(id);
        playerService.actualizarPerfil(jugador, foto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cambiar disponibilidad", description = "Cambia el estado de disponibilidad del jugador")
    @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada")
    @PutMapping("/{id}/disponibilidad")
    public ResponseEntity<Void> cambiarDisponibilidad(
            @Parameter(description = "ID del jugador") @PathVariable String id,
            @Parameter(description = "Estado de disponibilidad") @RequestParam boolean disponible) {

        log.info("PUT /api/players/{}/disponibilidad — disponible: {}", id, disponible);
        Player jugador = playerService.obtenerPorId(id);
        playerService.cambiarDisponibilidad(jugador, disponible);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar jugadores", description = "Obtiene todos los jugadores")
    @ApiResponse(responseCode = "200", description = "Lista de jugadores")
    @GetMapping
    public ResponseEntity<List<Player>> listar() {
        log.info("GET /api/players");
        return ResponseEntity.ok(playerService.listarJugadores());
    }

    @Operation(summary = "Buscar jugador por ID", description = "Obtiene un jugador por su ID")
    @ApiResponse(responseCode = "200", description = "Jugador encontrado")
    @ApiResponse(responseCode = "404", description = "Jugador no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<Player> buscarPorId(
            @Parameter(description = "ID del jugador") @PathVariable String id) {

        log.info("GET /api/players/{}", id);
        Optional<Player> resultado = playerService.buscarPorId(id);
        return resultado.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar jugador", description = "Elimina un jugador por ID")
    @ApiResponse(responseCode = "204", description = "Jugador eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del jugador") @PathVariable String id) {

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