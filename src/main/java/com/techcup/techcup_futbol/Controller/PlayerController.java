package com.techcup.techcup_futbol.Controller;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@Tag(name = "Players", description = "ENDPOINTS para la gestión de jugadores")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // Registro
    @PostMapping("/registro")
    @Operation(summary = "Registrar jugador", description = "Permite registar un juador en TECHUP")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Jugador registado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Error en el registro, datos inválidos")
    })
    public ResponseEntity<String> registrar(@RequestBody Player jugador, @RequestParam String correo) {
        playerService.registrar(jugador, correo);
        return new ResponseEntity<>("Jugador registrado exitosamente", HttpStatus.CREATED);
    }

    // Actualizar Perfil
    @PutMapping("/{id}/perfil")
    @Operation(summary = "Actualizar perfil del jugador",description = "Permite actualizar la info y foto del jugador")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Datos actualizados"),
            @ApiResponse(responseCode = "404", description = "Error en actualizar")
    })
    public ResponseEntity<Void> actualizarPerfil(@PathVariable String id, @RequestBody String foto) {
        // Primero buscamos al jugador por ID
        playerService.buscarPorId(id).ifPresent(jugador -> {
            playerService.actualizarPerfil(jugador, foto);
        });
        return ResponseEntity.ok().build();
    }

    // Cambiar Disponibilidad
    @PutMapping("/{id}/disponibilidad")
    @Operation(summary = "Cambiar disponibilidad del jugador", description = "Cambia el estado de disponibilidad del jugador")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada"),
            @ApiResponse(responseCode = "404", description = "Jugador inexistente")
    })
    public ResponseEntity<Void> cambiarDisponibilidad(@PathVariable String id) {
        playerService.buscarPorId(id).ifPresent(jugador -> {
            playerService.cambiarDisponibilidad(jugador);
        });
        return ResponseEntity.ok().build();
    }

    // Listar
    @GetMapping
    @Operation(summary = "Listar los jugadores", description ="Obtiene la lista de los jugadores registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jugador registado exitosamente")
    })
    public ResponseEntity<List<Player>> listar() {
        return ResponseEntity.ok(playerService.listarJugadores());
    }

    // Eliminar
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar jugador", description = "Elimina un jugador del sistema por su ID")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Jugador eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Jugador no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        playerService.eliminarJugador(id);
        return ResponseEntity.noContent().build();
    }
}