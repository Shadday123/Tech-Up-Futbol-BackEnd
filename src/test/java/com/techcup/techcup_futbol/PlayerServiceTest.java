package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.PlayerServiceImpl;
import com.techcup.techcup_futbol.exception.PlayerException;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Player Service Tests")
class PlayerServiceTest {

    private PlayerServiceImpl playerService;
    private Player jugadorPersistido;
    private String idPersistido;

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
        playerService = new PlayerServiceImpl();

        jugadorPersistido = new StudentPlayer();
        idPersistido = UUID.randomUUID().toString();
        jugadorPersistido.setId(idPersistido);
        jugadorPersistido.setFullname("Jugador Test");
        jugadorPersistido.setEmail("test@escuelaing.edu.co");
        jugadorPersistido.setNumberID(999999);
        jugadorPersistido.setPosition(PositionEnum.Midfielder);
        jugadorPersistido.setDorsalNumber(10);
        jugadorPersistido.setAge(20);
        jugadorPersistido.setGender("Masculino");
        jugadorPersistido.setHaveTeam(false);
        ((StudentPlayer) jugadorPersistido).setSemester(5);
        DataStore.jugadores.put(idPersistido, jugadorPersistido);
    }

    // ── Happy Path ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Happy Path")
    class HappyPathTests {

        @Test
        @DisplayName("HP-01: Registrar con correo institucional")
        void testRegistrar_CorreoInstitucional_Exitoso() {
            StudentPlayer nuevo = buildStudent(UUID.randomUUID().toString(),
                    "nuevo@escuelaing.edu.co", 111111);

            playerService.registrar(nuevo, "nuevo@escuelaing.edu.co");

            List<Player> jugadores = playerService.listarJugadores();
            assertEquals(2, jugadores.size());
            assertTrue(jugadores.stream()
                    .anyMatch(p -> p.getEmail().equals("nuevo@escuelaing.edu.co")));
        }

        @Test
        @DisplayName("HP-02: Registrar con correo Gmail")
        void testRegistrar_CorreoGmail_Exitoso() {
            InstitutionalPlayer nuevo = new InstitutionalPlayer();
            nuevo.setId(UUID.randomUUID().toString());
            nuevo.setFullname("Gmail Player");
            nuevo.setAge(25);
            nuevo.setGender("Femenino");
            nuevo.setNumberID(222222);

            playerService.registrar(nuevo, "gmail@gmail.com");

            assertTrue(playerService.listarJugadores().stream()
                    .anyMatch(p -> p.getEmail().equals("gmail@gmail.com")));
        }

        @Test
        @DisplayName("HP-03: Actualizar foto de perfil")
        void testActualizarPerfil_Exitoso() {
            playerService.actualizarPerfil(jugadorPersistido, "nueva_foto.jpg");

            assertEquals("nueva_foto.jpg",
                    playerService.obtenerPorId(idPersistido).getPhotoUrl());
        }

        @Test
        @DisplayName("HP-04: Cambiar disponibilidad a no disponible")
        void testCambiarDisponibilidad_ANoDisponible_Exitoso() {
            playerService.cambiarDisponibilidad(jugadorPersistido, false);

            assertTrue(playerService.obtenerPorId(idPersistido).isHaveTeam());
        }

        @Test
        @DisplayName("HP-05: Listar jugadores retorna lista correcta")
        void testListarJugadores_RetornaLista() {
            List<Player> resultado = playerService.listarJugadores();

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals("Jugador Test", resultado.get(0).getFullname());
        }

        @Test
        @DisplayName("HP-06: Buscar jugador existente por ID")
        void testBuscarPorId_JugadorExiste() {
            Optional<Player> resultado = playerService.buscarPorId(idPersistido);

            assertTrue(resultado.isPresent());
            assertEquals("Jugador Test", resultado.get().getFullname());
        }

        @Test
        @DisplayName("HP-07: obtenerPorId retorna jugador existente")
        void testObtenerPorId_Exitoso() {
            Player resultado = playerService.obtenerPorId(idPersistido);

            assertNotNull(resultado);
            assertEquals(idPersistido, resultado.getId());
        }

        @Test
        @DisplayName("HP-08: Eliminar jugador existente")
        void testEliminarJugador_Exitoso() {
            playerService.eliminarJugador(idPersistido);

            assertEquals(0, playerService.listarJugadores().size());
            assertFalse(playerService.buscarPorId(idPersistido).isPresent());
        }
    }

    // ── Error Path ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Error Path")
    class ErrorPathTests {

        @Test
        @DisplayName("EP-01: Correo de dominio inválido lanza PlayerException")
        void testRegistrar_CorreoInvalido_LanzaExcepcion() {
            StudentPlayer jugador = buildStudent(UUID.randomUUID().toString(),
                    "test@hotmail.com", 111001);

            assertThrows(PlayerException.class, () ->
                    playerService.registrar(jugador, "test@hotmail.com")
            );
            assertEquals(1, playerService.listarJugadores().size());
        }

        @Test
        @DisplayName("EP-02: Correo nulo lanza PlayerException")
        void testRegistrar_CorreoNulo_LanzaExcepcion() {
            StudentPlayer jugador = buildStudent(UUID.randomUUID().toString(), null, 111002);

            assertThrows(PlayerException.class, () ->
                    playerService.registrar(jugador, null)
            );
        }

        @Test
        @DisplayName("EP-03: Correo vacío lanza PlayerException")
        void testRegistrar_CorreoVacio_LanzaExcepcion() {
            StudentPlayer jugador = buildStudent(UUID.randomUUID().toString(), "", 111003);

            assertThrows(PlayerException.class, () ->
                    playerService.registrar(jugador, "")
            );
        }

        @Test
        @DisplayName("EP-04: ID interno nulo lanza PlayerException")
        void testRegistrar_IdNulo_LanzaExcepcion() {
            StudentPlayer jugador = buildStudent(null, "nuevo@escuelaing.edu.co", 111004);

            assertThrows(PlayerException.class, () ->
                    playerService.registrar(jugador, "nuevo@escuelaing.edu.co")
            );
        }

        @Test
        @DisplayName("EP-05: Email duplicado lanza PlayerException")
        void testRegistrar_EmailDuplicado_LanzaExcepcion() {
            StudentPlayer dup = buildStudent(UUID.randomUUID().toString(),
                    "test@escuelaing.edu.co", 555555);

            assertThrows(PlayerException.class, () ->
                    playerService.registrar(dup, "test@escuelaing.edu.co")
            );
        }

        @Test
        @DisplayName("EP-06: numberID duplicado lanza PlayerException")
        void testRegistrar_IDDuplicado_LanzaExcepcion() {
            StudentPlayer dup = buildStudent(UUID.randomUUID().toString(),
                    "otro@escuelaing.edu.co", 999999);

            assertThrows(PlayerException.class, () ->
                    playerService.registrar(dup, "otro@escuelaing.edu.co")
            );
        }

        @Test
        @DisplayName("EP-07: Actualizar perfil de jugador inexistente lanza PlayerException")
        void testActualizarPerfil_JugadorNoExiste_LanzaExcepcion() {
            Player fantasma = new StudentPlayer();
            fantasma.setId("ID_FANTASMA");

            assertThrows(PlayerException.class, () ->
                    playerService.actualizarPerfil(fantasma, "foto.jpg")
            );
        }

        @Test
        @DisplayName("EP-08: buscarPorId con ID inexistente retorna Optional vacío")
        void testBuscarPorId_NoExiste_RetornaEmpty() {
            assertFalse(playerService.buscarPorId("ID_INEXISTENTE").isPresent());
        }

        @Test
        @DisplayName("EP-09: obtenerPorId con ID inexistente lanza PlayerException")
        void testObtenerPorId_NoExiste_LanzaExcepcion() {
            assertThrows(PlayerException.class, () ->
                    playerService.obtenerPorId("ID_INEXISTENTE")
            );
        }

        @Test
        @DisplayName("EP-10: Eliminar jugador inexistente lanza PlayerException")
        void testEliminarJugador_NoExiste_LanzaExcepcion() {
            assertThrows(PlayerException.class, () ->
                    playerService.eliminarJugador("ID_INEXISTENTE")
            );
        }

        @Test
        @DisplayName("EP-11: Cambiar disponibilidad al mismo estado lanza PlayerException")
        void testCambiarDisponibilidad_MismoEstado_LanzaExcepcion() {
            // jugadorPersistido tiene haveTeam=false → ya está disponible
            assertThrows(PlayerException.class, () ->
                    playerService.cambiarDisponibilidad(jugadorPersistido, true)
            );
        }
    }

    // ── Conditional ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalTests {

        @Test
        @DisplayName("CS-01: Múltiples registros con dominios distintos")
        void testMultiplesRegistros_DominiosMixtos() {
            StudentPlayer est = buildStudent(UUID.randomUUID().toString(),
                    "est@escuelaing.edu.co", 333333);
            InstitutionalPlayer inst = new InstitutionalPlayer();
            inst.setId(UUID.randomUUID().toString());
            inst.setFullname("Institucional");
            inst.setAge(25);
            inst.setGender("Masculino");
            inst.setNumberID(444444);

            playerService.registrar(est,  "est@escuelaing.edu.co");
            playerService.registrar(inst, "inst@gmail.com");

            assertEquals(3, playerService.listarJugadores().size());
        }

        @Test
        @DisplayName("CS-02: Buscar después de eliminar retorna vacío")
        void testBuscarDespuesDeEliminar() {
            playerService.eliminarJugador(idPersistido);

            assertFalse(playerService.buscarPorId(idPersistido).isPresent());
        }

        @Test
        @DisplayName("CS-03: listarJugadores retorna copia defensiva")
        void testListarJugadores_CopiaDefensiva() {
            List<Player> lista = playerService.listarJugadores();
            lista.clear();

            assertEquals(1, playerService.listarJugadores().size());
        }

        @Test
        @DisplayName("CS-04: Cambiar disponibilidad ciclo completo")
        void testCambiarDisponibilidad_CicloCompleto() {
            playerService.cambiarDisponibilidad(jugadorPersistido, false);
            assertTrue(playerService.obtenerPorId(idPersistido).isHaveTeam());

            playerService.cambiarDisponibilidad(jugadorPersistido, true);
            assertFalse(playerService.obtenerPorId(idPersistido).isHaveTeam());
        }


    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private StudentPlayer buildStudent(String id, String email, int numberID) {
        StudentPlayer p = new StudentPlayer();
        p.setId(id);
        p.setFullname("Test " + numberID);
        p.setEmail(email);
        p.setNumberID(numberID);
        p.setAge(20);
        p.setGender("Masculino");
        p.setSemester(3);
        p.setHaveTeam(false);
        return p;
    }
}