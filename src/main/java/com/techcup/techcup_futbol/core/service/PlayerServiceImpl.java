package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // CREATE
    @Override
    public void registrar(Player jugador, String correo) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("[{}] Iniciando registro de jugador", timestamp);
        log.info("Email proporcionado: {}", correo);
        log.info("Nombre del jugador: {}", jugador.getFullname());
        log.info("Número ID: {}", jugador.getNumberID());
        log.info("Edad: {}", jugador.getAge());
        log.info("Género: {}", jugador.getGender());
        log.info("Tipo de jugador: {}", jugador.getClass().getSimpleName());

        // Validación de dominio de email
        if (correo.endsWith("@escuelaing.edu.co")) {
            log.debug(" Email institucional válido: @escuelaing.edu.co");
        } else if (correo.endsWith("@gmail.com")) {
            log.debug(" Email personal válido: @gmail.com");
        } else {
            log.error("Dominio de email no permitido: {}", correo);
            log.error("Dominios permitidos: @escuelaing.edu.co, @gmail.com");
            throw new IllegalArgumentException("Correo no válido. Use @escuelaing.edu.co o @gmail.com");
        }

        // Validación de ID duplicado
        if (DataStore.jugadores.values().stream()
                .anyMatch(p -> p.getNumberID() == jugador.getNumberID())) {
            log.error(" Ya existe jugador con número ID: {}", jugador.getNumberID());
            throw new IllegalArgumentException("El número de ID ya está registrado");
        }

        // Validación de email duplicado
        if (DataStore.jugadores.values().stream()
                .anyMatch(p -> p.getEmail() != null && p.getEmail().equals(correo))) {
            log.error("FALLO: Ya existe jugador con email: {}", correo);
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Registrar el jugador
        jugador.setEmail(correo);
        DataStore.jugadores.put(jugador.getId(), jugador);

        log.info("ÉXITO: Jugador registrado correctamente");
        log.info("ID asignado: {}", jugador.getId());
        log.info("Email asignado: {}", correo);
        log.info("Total de jugadores en el sistema: {}", DataStore.jugadores.size());
    }

    // UPDATE - PERFIL
    @Override
    public void actualizarPerfil(Player jugador, String foto) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("[{}] Actualizando foto del jugador", timestamp);
        log.info("ID del jugador: {}", jugador.getId());
        log.info("Nombre del jugador: {}", jugador.getFullname());
        log.info("URL anterior: {}", jugador.getPhotoUrl());
        log.info("URL nueva: {}", foto);

        jugador.setPhotoUrl(foto);

        log.info(" Foto actualizada correctamente");
    }

    // UPDATE - DISPONIBILIDAD
    @Override
    public void cambiarDisponibilidad(Player jugador) {
        String timestamp = LocalDateTime.now().format(formatter);
        boolean disponibleAntes = !jugador.isHaveTeam();

        log.info("[{}] Cambiando disponibilidad del jugador", timestamp);
        log.info("ID del jugador: {}", jugador.getId());
        log.info("Nombre del jugador: {}", jugador.getFullname());
        log.info("Disponible ANTES: {}", disponibleAntes);

        jugador.changeAvailability();
        boolean disponibleDespues = !jugador.isHaveTeam();

        log.info("Disponible DESPUÉS: {}", disponibleDespues);
        log.info("Estado actualizado correctamente");
    }

    // READ - LISTAR TODOS
    @Override
    public List<Player> listarJugadores() {
        String timestamp = LocalDateTime.now().format(formatter);
        log.info("[{}] Listando todos los jugadores del sistema", timestamp);

        List<Player> jugadores = new ArrayList<>(DataStore.jugadores.values());

        log.info("Total de jugadores encontrados: {}", jugadores.size());

        if (!jugadores.isEmpty()) {
            log.debug("Primeros 5 jugadores:");
            jugadores.stream().limit(5).forEach(j ->
                    log.debug("  - {} (ID: {}, Email: {})", j.getFullname(), j.getId(), j.getEmail())
            );
        } else {
            log.warn("No hay jugadores registrados en el sistema");
        }

        return jugadores;
    }

    // READ - BUSCAR POR ID
    @Override
    public Optional<Player> buscarPorId(String id) {
        String timestamp = LocalDateTime.now().format(formatter);

        log.info("[{}] Buscando jugador con ID: {}", timestamp, id);

        Optional<Player> resultado = Optional.ofNullable(DataStore.jugadores.get(id));

        if (resultado.isPresent()) {
            Player jugador = resultado.get();
            log.info("Nombre: {}", jugador.getFullname());
            log.info("Email: {}", jugador.getEmail());
            log.info("Número ID: {}", jugador.getNumberID());
            log.info("Posición: {}", jugador.getPosition());
            log.info("Dorsal: {}", jugador.getDorsalNumber());
            log.info("Tiene equipo: {}", jugador.isHaveTeam());
        } else {
            log.warn("✗ NO ENCONTRADO");
            log.warn("El jugador con ID {} no existe en el sistema", id);
        }

        log.info("==========================================================");
        return resultado;
    }

    // DELETE
    @Override
    public void eliminarJugador(String id) {
        String timestamp = LocalDateTime.now().format(formatter);
        log.info("[{}] Intentando eliminar jugador", timestamp);
        log.info("ID del jugador: {}", id);

        Optional<Player> jugador = Optional.ofNullable(DataStore.jugadores.get(id));

        if (jugador.isPresent()) {
            log.info("Jugador encontrado: {}", jugador.get().getFullname());
            log.info("Email: {}", jugador.get().getEmail());
            DataStore.jugadores.remove(id);
            log.info(" Jugador eliminado exitosamente");
        } else {
            log.warn(" No se puede eliminar - Jugador no encontrado");
        }

        log.info("Total de jugadores después de eliminar: {}", DataStore.jugadores.size());
    }
}