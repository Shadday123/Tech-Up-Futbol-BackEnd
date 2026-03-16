package com.techcup.techcup_futbol.Controller;


import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // Registro
    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@RequestBody Player jugador, @RequestParam String correo) {
        playerService.registrar(jugador, correo);
        return new ResponseEntity<>("Jugador registrado exitosamente", HttpStatus.CREATED);
    }

    // Actualizar Perfil
    @PutMapping("/{id}/perfil")
    public ResponseEntity<Void> actualizarPerfil(@PathVariable String id, @RequestBody String foto) {
        // Primero buscamos al jugador por ID
        playerService.buscarPorId(id).ifPresent(jugador -> {
            playerService.actualizarPerfil(jugador, foto);
        });
        return ResponseEntity.ok().build();
    }

    // Cambiar Disponibilidad
    @PutMapping("/{id}/disponibilidad")
    public ResponseEntity<Void> cambiarDisponibilidad(@PathVariable String id) {
        playerService.buscarPorId(id).ifPresent(jugador -> {
            playerService.cambiarDisponibilidad(jugador);
        });
        return ResponseEntity.ok().build();
    }

    // Listar
    @GetMapping
    public ResponseEntity<List<Player>> listar() {
        return ResponseEntity.ok(playerService.listarJugadores());
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        playerService.eliminarJugador(id);
        return ResponseEntity.noContent().build();
    }
}