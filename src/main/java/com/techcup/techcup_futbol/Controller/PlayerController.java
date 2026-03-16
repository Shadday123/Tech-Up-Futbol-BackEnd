package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private static final Logger log = LoggerFactory.getLogger(PlayerController.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // Registro
    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@RequestBody Player jugador, @RequestParam String correo) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("ENDPOINT: POST /api/players/registro");

        log.info("[{}] Solicitud recibida", timestamp);
        log.info("IP del cliente: {}", getClientIp());
        log.info("Datos del jugador:");
        log.info("  - Nombre: {}", jugador.getFullname());
        log.info("  - Email: {}", correo);
        log.info("  - Número ID: {}", jugador.getNumberID());

        try {
            playerService.registrar(jugador, correo);
            log.info("Registro completado exitosamente");
            log.info("Respuesta: HTTP 201 Created");
            return new ResponseEntity<>("Jugador registrado exitosamente", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error en el registro");
            log.error("Excepción: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Actualizar Perfil
    @PutMapping("/{id}/perfil")
    public ResponseEntity<Void> actualizarPerfil(@PathVariable String id, @RequestBody String foto) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("ENDPOINT: PUT /api/players/{id}/perfil");
        log.info("[{}] Solicitud recibida", timestamp);
        log.info("ID del jugador: {}", id);
        log.info("Nueva URL de foto: {}", foto);

        try {
            playerService.buscarPorId(id).ifPresentOrElse(
                    jugador -> {
                        playerService.actualizarPerfil(jugador, foto);
                        log.info("Perfil actualizado exitosamente");
                        log.info("Jugador: {}", jugador.getFullname());
                        log.info("Respuesta: HTTP 200 OK");
                    },
                    () -> {
                        log.warn("Jugador no encontrado");
                        log.warn("ID solicitado: {}", id);
                    }
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar perfil");
            log.error("Excepción: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Cambiar Disponibilidad
    @PutMapping("/{id}/disponibilidad")
    public ResponseEntity<Void> cambiarDisponibilidad(@PathVariable String id) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("ENDPOINT: PUT /api/players/{id}/disponibilidad");
        log.info("[{}] Solicitud recibida", timestamp);
        log.info("ID del jugador: {}", id);

        try {
            playerService.buscarPorId(id).ifPresentOrElse(
                    jugador -> {
                        boolean estabaDisponible = !jugador.isHaveTeam();
                        playerService.cambiarDisponibilidad(jugador);
                        log.info("Disponibilidad cambiada");
                        log.info("Jugador: {}", jugador.getFullname());
                        log.info("Estado anterior: {}", estabaDisponible ? "Disponible" : "En equipo");
                        log.info("Estado nuevo: {}", !estabaDisponible ? "Disponible" : "En equipo");
                        log.info("Respuesta: HTTP 200 OK");
                    },
                    () -> {
                        log.warn(" Jugador no encontrado");
                        log.warn("ID solicitado: {}", id);
                    }
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(" Error al cambiar disponibilidad");
            log.error("Excepción: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Listar
    @GetMapping
    public ResponseEntity<List<Player>> listar() {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("ENDPOINT: GET /api/players");

        log.info("[{}] Solicitud recibida", timestamp);
        log.info("Acción: Listar todos los jugadores");

        try {
            List<Player> jugadores = playerService.listarJugadores();
            log.info("Listado completado");
            log.info("Total de jugadores: {}", jugadores.size());
            log.info("Respuesta: HTTP 200 OK");
            return ResponseEntity.ok(jugadores);
        } catch (Exception e) {
            log.error("Error al listar jugadores");
            log.error("Excepción: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("ENDPOINT: DELETE /api/players/{id}");
        log.info("[{}] Solicitud recibida", timestamp);
        log.info("ID del jugador a eliminar: {}", id);

        try {
            playerService.buscarPorId(id).ifPresentOrElse(
                    jugador -> {
                        playerService.eliminarJugador(id);
                        log.info("Jugador eliminado");
                        log.info("Jugador: {}", jugador.getFullname());
                        log.info("Respuesta: HTTP 204 No Content");
                    },
                    () -> {
                        log.warn("Jugador no encontrado");
                        log.warn("ID solicitado: {}", id);
                    }
            );
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error al eliminar jugador");
            log.error("Excepción: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Método auxiliar para obtener IP del cliente
    private String getClientIp() {
        return "127.0.0.1"; // En producción obtener del request
    }
}