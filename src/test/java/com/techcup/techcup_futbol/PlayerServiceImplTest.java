package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.PlayerException;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.PlayerServiceImpl;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PlayerServiceImpl Tests")
class PlayerServiceImplTest {

    private PlayerServiceImpl service;

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
        service = new PlayerServiceImpl();
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-PS-01: registrar() guarda jugador en DataStore correctamente")
        void registrarGuardaJugador() {
            StudentPlayer p = buildStudent("test@escuelaing.edu.co", 111001, "Test Player", 20);
            service.registrar(p, "test@escuelaing.edu.co");

            assertEquals(1, DataStore.jugadores.size());
            assertEquals("test@escuelaing.edu.co", DataStore.jugadores.get(p.getId()).getEmail());
        }

        @Test
        @DisplayName("HP-PS-02: registrar() auto-genera UUID si ID es null")
        void registrarAutoGeneraIdNull() {
            StudentPlayer p = buildStudent("auto@escuelaing.edu.co", 111002, "Auto ID", 21);
            p.setId(null);
            service.registrar(p, "auto@escuelaing.edu.co");

            assertNotNull(p.getId());
            assertFalse(p.getId().isBlank());
        }

        @Test
        @DisplayName("HP-PS-03: registrar() auto-genera UUID si ID está en blanco")
        void registrarAutoGeneraIdBlanco() {
            StudentPlayer p = buildStudent("blank@escuelaing.edu.co", 111003, "Blank ID", 22);
            p.setId("   ");
            service.registrar(p, "blank@escuelaing.edu.co");

            assertNotNull(p.getId());
            assertFalse(p.getId().isBlank());
        }

        @Test
        @DisplayName("HP-PS-04: registrar() asigna el correo al jugador")
        void registrarAsignaCorreo() {
            StudentPlayer p = buildStudent("correo@escuelaing.edu.co", 111004, "Correo Test", 20);
            service.registrar(p, "correo@escuelaing.edu.co");

            assertEquals("correo@escuelaing.edu.co", p.getEmail());
        }

        @Test
        @DisplayName("HP-PS-05: listarJugadores() retorna todos los jugadores del DataStore")
        void listarJugadoresRetornaTodos() {
            service.registrar(buildStudent("j1@escuelaing.edu.co", 200001, "J1", 20), "j1@escuelaing.edu.co");
            service.registrar(buildStudent("j2@gmail.com", 200002, "J2", 25), "j2@gmail.com");

            List<Player> lista = service.listarJugadores();
            assertEquals(2, lista.size());
        }

        @Test
        @DisplayName("HP-PS-06: listarJugadores() retorna lista vacía si no hay jugadores")
        void listarJugadoresRetornaVacio() {
            List<Player> lista = service.listarJugadores();
            assertTrue(lista.isEmpty());
        }

        @Test
        @DisplayName("HP-PS-07: buscarPorId() retorna Optional con jugador si existe")
        void buscarPorIdRetornaJugador() {
            StudentPlayer p = buildStudent("buscar@escuelaing.edu.co", 300001, "Buscar", 20);
            DataStore.jugadores.put(p.getId(), p);

            Optional<Player> resultado = service.buscarPorId(p.getId());
            assertTrue(resultado.isPresent());
            assertEquals(p.getFullname(), resultado.get().getFullname());
        }

        @Test
        @DisplayName("HP-PS-08: buscarPorId() retorna Optional vacío si no existe")
        void buscarPorIdRetornaVacio() {
            Optional<Player> resultado = service.buscarPorId("NO-EXISTE");
            assertFalse(resultado.isPresent());
        }

        @Test
        @DisplayName("HP-PS-09: obtenerPorId() retorna jugador si existe")
        void obtenerPorIdRetornaJugador() {
            StudentPlayer p = buildStudent("obtener@escuelaing.edu.co", 400001, "Obtener", 20);
            DataStore.jugadores.put(p.getId(), p);

            Player resultado = service.obtenerPorId(p.getId());
            assertEquals(p.getId(), resultado.getId());
        }

        @Test
        @DisplayName("HP-PS-10: eliminarJugador() remueve el jugador del DataStore")
        void eliminarJugadorRemueveDatos() {
            StudentPlayer p = buildStudent("eliminar@escuelaing.edu.co", 500001, "Eliminar", 20);
            DataStore.jugadores.put(p.getId(), p);

            service.eliminarJugador(p.getId());

            assertFalse(DataStore.jugadores.containsKey(p.getId()));
        }

        @Test
        @DisplayName("HP-PS-11: actualizarPerfil() cambia la foto del jugador")
        void actualizarPerfilCambiaFoto() {
            StudentPlayer p = buildStudent("foto@escuelaing.edu.co", 600001, "Foto Test", 20);
            DataStore.jugadores.put(p.getId(), p);

            service.actualizarPerfil(p, "nueva-foto.jpg");

            assertEquals("nueva-foto.jpg", DataStore.jugadores.get(p.getId()).getPhotoUrl());
        }

        @Test
        @DisplayName("HP-PS-12: cambiarDisponibilidad() de disponible (false) a no disponible (true)")
        void cambiarDisponibilidadANoDisponible() {
            StudentPlayer p = buildStudent("disp@escuelaing.edu.co", 700001, "Disp Test", 20);
            p.setHaveTeam(false); // disponible
            DataStore.jugadores.put(p.getId(), p);

            service.cambiarDisponibilidad(p, false); // pedir no disponible

            assertTrue(DataStore.jugadores.get(p.getId()).isHaveTeam());
        }

        @Test
        @DisplayName("HP-PS-13: cambiarDisponibilidad() de no disponible (true) a disponible (false)")
        void cambiarDisponibilidadADisponible() {
            StudentPlayer p = buildStudent("disp2@escuelaing.edu.co", 700002, "Disp2 Test", 20);
            p.setHaveTeam(true); // no disponible
            DataStore.jugadores.put(p.getId(), p);

            service.cambiarDisponibilidad(p, true); // pedir disponible

            assertFalse(DataStore.jugadores.get(p.getId()).isHaveTeam());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-PS-01: registrar() lanza PlayerException si nombre está vacío")
        void registrarFallaConNombreVacio() {
            StudentPlayer p = buildStudent("err@escuelaing.edu.co", 800001, "", 20);
            assertThrows(PlayerException.class,
                    () -> service.registrar(p, "err@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("EP-PS-02: registrar() lanza PlayerException si correo tiene dominio inválido")
        void registrarFallaConDominioInvalido() {
            StudentPlayer p = buildStudent("err@hotmail.com", 800002, "Err User", 20);
            assertThrows(PlayerException.class,
                    () -> service.registrar(p, "err@hotmail.com"));
        }

        @Test
        @DisplayName("EP-PS-03: registrar() lanza PlayerException si correo ya existe")
        void registrarFallaConCorreoDuplicado() {
            StudentPlayer existente = buildStudent("dup@escuelaing.edu.co", 800003, "Existente", 20);
            DataStore.jugadores.put(existente.getId(), existente);

            StudentPlayer nuevo = buildStudent("dup@escuelaing.edu.co", 800004, "Nuevo", 21);
            assertThrows(PlayerException.class,
                    () -> service.registrar(nuevo, "dup@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("EP-PS-04: registrar() lanza PlayerException si edad inválida")
        void registrarFallaConEdadInvalida() {
            StudentPlayer p = buildStudent("edad@escuelaing.edu.co", 800005, "Edad Test", 10);
            assertThrows(PlayerException.class,
                    () -> service.registrar(p, "edad@escuelaing.edu.co"));
        }

        @Test
        @DisplayName("EP-PS-05: obtenerPorId() lanza PlayerException si jugador no existe")
        void obtenerPorIdLanzaExcepcion() {
            PlayerException ex = assertThrows(PlayerException.class,
                    () -> service.obtenerPorId("NO-EXISTE"));
            assertEquals("id", ex.getField());
            assertTrue(ex.getMessage().contains("NO-EXISTE"));
        }

        @Test
        @DisplayName("EP-PS-06: eliminarJugador() lanza PlayerException si no existe")
        void eliminarJugadorLanzaExcepcion() {
            assertThrows(PlayerException.class,
                    () -> service.eliminarJugador("ID-FANTASMA"));
        }

        @Test
        @DisplayName("EP-PS-07: actualizarPerfil() lanza PlayerException si jugador no existe")
        void actualizarPerfilLanzaExcepcion() {
            StudentPlayer fantasma = buildStudent("fantasma@escuelaing.edu.co", 900001, "Fantasma", 20);
            // no se agrega al DataStore
            assertThrows(PlayerException.class,
                    () -> service.actualizarPerfil(fantasma, "foto.jpg"));
        }

        @Test
        @DisplayName("EP-PS-08: cambiarDisponibilidad() lanza excepción si ya está disponible")
        void cambiarDisponibilidadYaDisponible() {
            StudentPlayer p = buildStudent("ya@escuelaing.edu.co", 900002, "Ya Disp", 20);
            p.setHaveTeam(false); // ya disponible
            DataStore.jugadores.put(p.getId(), p);

            PlayerException ex = assertThrows(PlayerException.class,
                    () -> service.cambiarDisponibilidad(p, true)); // pedir disponible de nuevo
            assertEquals("availability", ex.getField());
        }

        @Test
        @DisplayName("EP-PS-09: cambiarDisponibilidad() lanza excepción si ya está no disponible")
        void cambiarDisponibilidadYaNoDisponible() {
            StudentPlayer p = buildStudent("ya2@escuelaing.edu.co", 900003, "Ya No Disp", 20);
            p.setHaveTeam(true); // ya no disponible
            DataStore.jugadores.put(p.getId(), p);

            PlayerException ex = assertThrows(PlayerException.class,
                    () -> service.cambiarDisponibilidad(p, false)); // pedir no disponible de nuevo
            assertEquals("availability", ex.getField());
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-PS-01: registrar() incrementa el tamaño del DataStore en 1")
        void registrarIncrementaDataStore() {
            int antes = DataStore.jugadores.size();
            service.registrar(buildStudent("incr@escuelaing.edu.co", 111010, "Incr Test", 20),
                    "incr@escuelaing.edu.co");
            assertEquals(antes + 1, DataStore.jugadores.size());
        }

        @Test
        @DisplayName("CS-PS-02: eliminarJugador() decrementa el DataStore en 1")
        void eliminarDecrementaDataStore() {
            StudentPlayer p = buildStudent("dec@escuelaing.edu.co", 111011, "Dec Test", 20);
            DataStore.jugadores.put(p.getId(), p);
            int antes = DataStore.jugadores.size();

            service.eliminarJugador(p.getId());

            assertEquals(antes - 1, DataStore.jugadores.size());
        }

        @Test
        @DisplayName("CS-PS-03: actualizarPerfil() no modifica otros campos del jugador")
        void actualizarPerfilNoModificaOtrosCampos() {
            StudentPlayer p = buildStudent("otros@escuelaing.edu.co", 111012, "Otros Campos", 25);
            DataStore.jugadores.put(p.getId(), p);

            service.actualizarPerfil(p, "foto-nueva.png");

            Player persistido = DataStore.jugadores.get(p.getId());
            assertEquals("Otros Campos", persistido.getFullname());
            assertEquals(25, persistido.getAge());
        }

        @Test
        @DisplayName("CS-PS-04: registrar() con gmail es válido para RelativePlayer")
        void registrarGmailRelativePlayer() {
            RelativePlayer p = new RelativePlayer();
            p.setId(UUID.randomUUID().toString());
            p.setFullname("Familiar Gmail");
            p.setNumberID(111013);
            p.setAge(30);
            p.setGender("Femenino");

            assertDoesNotThrow(() -> service.registrar(p, "familiar@gmail.com"));
            assertTrue(DataStore.jugadores.containsKey(p.getId()));
        }

        @Test
        @DisplayName("CS-PS-05: buscarPorId() no lanza excepción cuando no encuentra, retorna vacío")
        void buscarPorIdNoLanzaExcepcion() {
            assertDoesNotThrow(() -> {
                Optional<Player> r = service.buscarPorId("INEXISTENTE");
                assertFalse(r.isPresent());
            });
        }

        @Test
        @DisplayName("CS-PS-06: registrar múltiples jugadores todos quedan en DataStore")
        void registrarMultiplesJugadores() {
            service.registrar(buildStudent("m1@escuelaing.edu.co", 111020, "M1", 20), "m1@escuelaing.edu.co");
            service.registrar(buildStudent("m2@escuelaing.edu.co", 111021, "M2", 21), "m2@escuelaing.edu.co");
            service.registrar(buildStudent("m3@gmail.com", 111022, "M3", 22), "m3@gmail.com");

            assertEquals(3, DataStore.jugadores.size());
        }

        @Test
        @DisplayName("CS-PS-07: cambiarDisponibilidad() no afecta otros jugadores en el DataStore")
        void cambiarDisponibilidadNoAfectaOtros() {
            StudentPlayer p1 = buildStudent("p1@escuelaing.edu.co", 111030, "P1", 20);
            StudentPlayer p2 = buildStudent("p2@escuelaing.edu.co", 111031, "P2", 21);
            p1.setHaveTeam(false);
            p2.setHaveTeam(false);
            DataStore.jugadores.put(p1.getId(), p1);
            DataStore.jugadores.put(p2.getId(), p2);

            service.cambiarDisponibilidad(p1, false);

            assertFalse(DataStore.jugadores.get(p2.getId()).isHaveTeam()); // p2 sin cambios
        }
    }

    // ── Helpers

    private StudentPlayer buildStudent(String email, int numberID, String name, int age) {
        StudentPlayer p = new StudentPlayer();
        p.setId(UUID.randomUUID().toString());
        p.setFullname(name);
        p.setEmail(email);
        p.setNumberID(numberID);
        p.setAge(age);
        p.setGender("Masculino");
        p.setSemester(3);
        p.setDorsalNumber(10);
        p.setPosition(PositionEnum.Midfielder);
        return p;
    }
}
