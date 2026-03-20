package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.validator.EmailValidator;
import com.techcup.techcup_futbol.core.validator.PlayerValidator;
import com.techcup.techcup_futbol.core.exception.PlayerException;
import com.techcup.techcup_futbol.util.IdGenerator;
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
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // CREATE

    @Override
    public void registrar(Player jugador, String correo) {
        String ts = LocalDateTime.now().format(FMT);

        if (jugador.getId() == null || jugador.getId().isBlank()) {
            jugador.setId(IdGenerator.generateId());
            log.debug("[{}] ID auto-generado: {}", ts, jugador.getId());
        }

        log.info("[{}] Iniciando registro — jugador: {} | email: {}",
                ts, jugador.getFullname(), correo);

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

    // UPDATE

    @Override
    public void actualizarPerfil(Player jugador, String foto) {
        String ts = LocalDateTime.now().format(FMT);
        log.info("[{}] Actualizando foto del jugador ID: {}", ts, jugador.getId());
        Player persistido = obtenerPorId(jugador.getId());
        log.debug("URL anterior: {} | URL nueva: {}", persistido.getPhotoUrl(), foto);
        persistido.setPhotoUrl(foto);
        log.info("Foto actualizada para jugador: {}", persistido.getFullname());
    }

    @Override
    public void cambiarDisponibilidad(Player jugador, boolean disponible) {
        String ts = LocalDateTime.now().format(FMT);
        log.info("[{}] Cambiando disponibilidad — jugador: {} | solicitado: {}",
                ts, jugador.getFullname(), disponible);

        Player persistido = obtenerPorId(jugador.getId());
        boolean estadoActual = !persistido.isHaveTeam(); // haveTeam=false → disponible=true

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

    // READ

    @Override
    public List<Player> listarJugadores() {
        log.info("[{}] Listando todos los jugadores del sistema", LocalDateTime.now().format(FMT));
        List<Player> jugadores = new ArrayList<>(DataStore.jugadores.values());
        if (jugadores.isEmpty()) {
            log.warn("No hay jugadores registrados en el sistema.");
        } else {
            log.info("Total de jugadores encontrados: {}", jugadores.size());
        }
        return jugadores;
    }

    @Override
    public Optional<Player> buscarPorId(String id) {
        log.info("[{}] Buscando jugador con ID: {}", LocalDateTime.now().format(FMT), id);
        Optional<Player> resultado = Optional.ofNullable(DataStore.jugadores.get(id));
        if (resultado.isPresent()) {
            log.info("Jugador encontrado — Nombre: {}", resultado.get().getFullname());
        } else {
            log.warn("No se encontró jugador con ID: {}", id);
        }
        return resultado;
    }

    @Override
    public Player obtenerPorId(String id) {
        return buscarPorId(id).orElseThrow(() ->
                new PlayerException("id", String.format(PlayerException.PLAYER_NOT_FOUND, id)));
    }

    // DELETE

    @Override
    public void eliminarJugador(String id) {
        log.info("[{}] Eliminando jugador con ID: {}", LocalDateTime.now().format(FMT), id);
        Player jugador = obtenerPorId(id);
        log.info("Eliminando jugador: {} | Email: {}", jugador.getFullname(), jugador.getEmail());
        DataStore.jugadores.remove(id);
        log.info("Jugador eliminado. Total restante: {}", DataStore.jugadores.size());
    }
}