package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.exception.PlayerException;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.PlayerServiceImpl;
import com.techcup.techcup_futbol.repository.PlayerRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PlayerServiceImpl Tests")
class PlayerServiceImplTest {

    @InjectMocks
    private PlayerServiceImpl service;

    @Mock
    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
        when(playerRepository.save(any(Player.class))).thenAnswer(inv -> {
            Player p = inv.getArgument(0);
            DataStore.jugadores.put(p.getId(), p);
            return p;
        });
        when(playerRepository.findById(anyString()))
                .thenAnswer(inv -> Optional.ofNullable(DataStore.jugadores.get(inv.getArgument(0))));
        when(playerRepository.findAll())
                .thenAnswer(inv -> new ArrayList<>(DataStore.jugadores.values()));
        doAnswer(inv -> {
            DataStore.jugadores.remove(inv.getArgument(0).toString());
            return null;
        }).when(playerRepository).deleteById(anyString());
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-PS-06: listarJugadores() retorna lista vacía si no hay jugadores")
        void listarJugadoresRetornaVacio() {
            List<Player> lista = service.listarJugadores();
            assertTrue(lista.isEmpty());
        }


        @Test
        @DisplayName("HP-PS-08: buscarPorId() retorna Optional vacío si no existe")
        void buscarPorIdRetornaVacio() {
            Optional<Player> resultado = service.buscarPorId("NO-EXISTE");
            assertFalse(resultado.isPresent());
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
    }}}
