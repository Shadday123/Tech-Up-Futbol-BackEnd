package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.validator.EmailValidator;
import com.techcup.techcup_futbol.core.validator.PlayerValidator;
import com.techcup.techcup_futbol.core.exception.PlayerException;
import com.techcup.techcup_futbol.persistence.entity.PlayerEntity;
import com.techcup.techcup_futbol.persistence.entity.StudentPlayerEntity;
import com.techcup.techcup_futbol.persistence.mapper.PlayerPersistenceMapper;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techcup.techcup_futbol.core.util.IdGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PlayerRepository playerRepository;
    private final PlayerValidator playerValidator;

    public PlayerServiceImpl(PlayerRepository playerRepository, PlayerValidator playerValidator) {
        this.playerRepository = playerRepository;
        this.playerValidator = playerValidator;
    }

    @Override
    @Transactional
    public void registrar(Player jugador, String correo) {
        if (playerRepository.existsByEmailIgnoreCase(correo)) {
            throw new PlayerException("email",
                    String.format(PlayerException.EMAIL_ALREADY_REGISTERED, correo));
        }

        jugador.setId(IdGenerator.generateId());
        log.debug("[{}] ID generado: {}", LocalDateTime.now().format(FMT), jugador.getId());

        log.info("[{}] Iniciando registro — jugador: {} | email: {}",
                LocalDateTime.now().format(FMT), jugador.getFullname(), correo);

        playerValidator.validate(jugador, correo);

        if (EmailValidator.esCorreoInstitucional(correo)) {
            log.debug("Email institucional detectado: {}", correo);
        } else {
            log.debug("Email personal (gmail) detectado: {}", correo);
        }

        jugador.setEmail(correo);

        PlayerEntity entity = PlayerPersistenceMapper.toEntity(jugador);
        playerRepository.save(entity);

        log.info("Jugador registrado — ID: {} | Email: {}", jugador.getId(), correo);
    }

    @Override
    @Transactional
    public void actualizarPerfil(Player jugador, String foto) {
        log.info("[{}] Actualizando foto del jugador ID: {}",
                LocalDateTime.now().format(FMT), jugador.getId());

        Player persistido = obtenerPorId(jugador.getId());
        log.debug("URL anterior: {} | URL nueva: {}", persistido.getPhotoUrl(), foto);
        persistido.setPhotoUrl(foto);

        PlayerEntity entity = PlayerPersistenceMapper.toEntity(persistido);
        playerRepository.save(entity);

        log.info("Foto actualizada para jugador: {}", persistido.getFullname());
    }

    @Override
    @Transactional
    public void cambiarDisponibilidad(Player jugador, boolean disponible) {
        log.info("[{}] Cambiando disponibilidad — jugador: {} | solicitado: {}",
                LocalDateTime.now().format(FMT), jugador.getFullname(), disponible);

        Player persistido = obtenerPorId(jugador.getId());

        if (persistido.isDisponible() == disponible) {
            String msg = disponible
                    ? String.format(PlayerException.PLAYER_ALREADY_AVAILABLE, persistido.getFullname())
                    : String.format(PlayerException.PLAYER_ALREADY_UNAVAILABLE, persistido.getFullname());
            throw new PlayerException("availability", msg);
        }

        persistido.setDisponible(disponible);

        PlayerEntity entity = PlayerPersistenceMapper.toEntity(persistido);
        playerRepository.save(entity);

        log.info("Disponibilidad actualizada — jugador: {} | disponible ahora: {}",
                persistido.getFullname(), disponible);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Player> listarJugadores() {
        log.info("[{}] Listando todos los jugadores del sistema", LocalDateTime.now().format(FMT));
        List<Player> jugadores = playerRepository.findAll()
                .stream()
                .map(PlayerPersistenceMapper::toDomain)
                .collect(Collectors.toList());
        if (jugadores.isEmpty()) {
            log.warn("No hay jugadores registrados en el sistema.");
        } else {
            log.info("Total de jugadores encontrados: {}", jugadores.size());
        }
        return jugadores;
    }

    @Transactional(readOnly = true)
    public List<Player> listarJugadoresSinEquipo() {
        log.info("[{}] Listando jugadores sin equipo", LocalDateTime.now().format(FMT));
        List<Player> disponibles = playerRepository.findByHaveTeamFalse()
                .stream()
                .map(PlayerPersistenceMapper::toDomain)
                .collect(Collectors.toList());
        log.info("Jugadores sin equipo encontrados: {}", disponibles.size());
        return disponibles;
    }

    @Transactional(readOnly = true)
    public List<Player> listarPorPosicion(PositionEnum posicion) {
        log.info("[{}] Listando jugadores por posición: {}",
                LocalDateTime.now().format(FMT), posicion);
        List<Player> jugadores = playerRepository.findByPosition(posicion)
                .stream()
                .map(PlayerPersistenceMapper::toDomain)
                .collect(Collectors.toList());
        log.info("Jugadores de {} encontrados: {}", posicion, jugadores.size());
        return jugadores;
    }

    @Transactional(readOnly = true)
    public List<Player> buscarPorEmail(String emailFragmento) {
        log.info("[{}] Buscando jugadores por email: {}",
                LocalDateTime.now().format(FMT), emailFragmento);
        List<Player> resultados = playerRepository.findByEmailContaining(emailFragmento)
                .stream()
                .map(PlayerPersistenceMapper::toDomain)
                .collect(Collectors.toList());
        log.info("Coincidencias de email encontradas: {}", resultados.size());
        return resultados;
    }

    @Transactional(readOnly = true)
    public List<Player> listarEstudiantesPorSemestre(Integer semestre) {
        log.info("[{}] Listando estudiantes del semestre: {}",
                LocalDateTime.now().format(FMT), semestre);
        List<Player> estudiantes = playerRepository.findBySemester(semestre)
                .stream()
                .map(PlayerPersistenceMapper::toDomain)
                .collect(Collectors.toList());
        log.info("Estudiantes del semestre {} encontrados: {}", semestre, estudiantes.size());
        return estudiantes;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Player> buscarPorId(String id) {
        log.info("[{}] Buscando jugador con ID: {}", LocalDateTime.now().format(FMT), id);
        Optional<Player> resultado = playerRepository.findById(id)
                .map(PlayerPersistenceMapper::toDomain);
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

    @Override
    @Transactional
    public void eliminarJugador(String id) {
        log.info("[{}] Eliminando jugador con ID: {}", LocalDateTime.now().format(FMT), id);
        Player jugador = obtenerPorId(id);
        log.info("Eliminando jugador: {} | Email: {}", jugador.getFullname(), jugador.getEmail());
        playerRepository.deleteById(id);
        log.info("Jugador eliminado correctamente");
    }
}