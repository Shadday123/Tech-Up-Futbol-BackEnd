package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.validator.EmailValidator;
import com.techcup.techcup_futbol.core.validator.PlayerValidator;
import com.techcup.techcup_futbol.exception.PlayerException;
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
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ── CREATE

    @Override
    public void registrar(Player jugador, String correo) {
        String ts = LocalDateTime.now().format(formatter);
        log.info("[{}] Iniciando registro — jugador: {} | email: {}",
                ts, jugador.getFullname(), correo);

        if (jugador.getId() == null || jugador.getId().isBlank()) {
            throw new PlayerException("id", PlayerException.PLAYER_ID_NULL);
        }

        PlayerValidator.validate(jugador, correo);

        if (EmailValidator.esCorreoInstitucional(correo)) {
            log.debug("Email institucional detectado: {}", correo);
        } else {
            log.debug("Email personal (gmail) detectado: {}", correo);
        }

        jugador.setEmail(correo);
        DataStore.jugadores.put(jugador.getId(), jugador);

        log.info("Jugador registrado — ID: {} | Email: {} | Total: {}",
                jugador.getId(), correo, DataStore.jugadores.size());
    }

    // ── UPDATE

    @Override
    public void actualizarPerfil(Player jugador, String foto) {
        String ts = LocalDateTime.now().format(formatter);
        log.info("[{}] Actualizando foto del jugador ID: {}", ts, jugador.getId());
        Player persistido = obtenerPorId(jugador.getId());

        log.debug("URL anterior: {} | URL nueva: {}", persistido.getPhotoUrl(), foto);
        persistido.setPhotoUrl(foto);
        log.info("Foto actualizada para jugador: {}", persistido.getFullname());
    }

    // ── UPDATE

    @Override
    public void cambiarDisponibilidad(Player jugador, boolean disponible) {
        String ts = LocalDateTime.now().format(formatter);
        log.info("[{}] Cambiando disponibilidad — jugador: {} | disponible solicitado: {}",
                ts, jugador.getFullname(), disponible);

        Player persistido = obtenerPorId(jugador.getId());

        boolean estadoActual = !persistido.isHaveTeam();

        if (estadoActual == disponible) {
            String msg = disponible
                    ? String.format(PlayerException.PLAYER_ALREADY_AVAILABLE, persistido.getFullname())
                    : String.format(PlayerException.PLAYER_ALREADY_UNAVAILABLE, persistido.getFullname());
            throw new PlayerException("availability", msg);
        }

        persistido.setHaveTeam(!disponible);
        log.info("Disponibilidad actualizada — jugador: {} | disponible ahora: {}",
                persistido.getFullname(), disponible);
    }

    // ── READ — LISTAR TODOS ───────────────────────────────────────────────────

    @Override
    public List<Player> listarJugadores() {
        String ts = LocalDateTime.now().format(formatter);
        log.info("[{}] Listando todos los jugadores del sistema", ts);

        List<Player> jugadores = new ArrayList<>(DataStore.jugadores.values());

        if (jugadores.isEmpty()) {
            log.warn("No hay jugadores registrados en el sistema.");
        } else {
            log.info("Total de jugadores encontrados: {}", jugadores.size());
            jugadores.stream().limit(5).forEach(j ->
                    log.debug("  → {} (ID: {}, Email: {})",
                            j.getFullname(), j.getId(), j.getEmail())
            );
        }

        return jugadores;
    }

    // ── READ — BUSCAR POR ID

    @Override
    public Optional<Player> buscarPorId(String id) {
        String ts = LocalDateTime.now().format(formatter);
        log.info("[{}] Buscando jugador con ID: {}", ts, id);

        Optional<Player> resultado = Optional.ofNullable(DataStore.jugadores.get(id));

        if (resultado.isPresent()) {
            Player j = resultado.get();
            log.info("Jugador encontrado — Nombre: {} | Email: {} | Dorsal: {} | Tiene equipo: {}",
                    j.getFullname(), j.getEmail(), j.getDorsalNumber(), j.isHaveTeam());
        } else {
            log.warn("No se encontró jugador con ID: {}", id);
        }

        return resultado;
    }

    // ── READ

    @Override
    public Player obtenerPorId(String id) {
        return buscarPorId(id).orElseThrow(() ->
                new PlayerException("id",
                        String.format(PlayerException.PLAYER_NOT_FOUND, id))
        );
    }

    // DELETE

    @Override
    public void eliminarJugador(String id) {
        String ts = LocalDateTime.now().format(formatter);
        log.info("[{}] Eliminando jugador con ID: {}", ts, id);

        Player jugador = obtenerPorId(id);

        log.info("Eliminando jugador: {} | Email: {}", jugador.getFullname(), jugador.getEmail());
        DataStore.jugadores.remove(id);
        log.info("Jugador eliminado. Total restante: {}", DataStore.jugadores.size());
    }
}