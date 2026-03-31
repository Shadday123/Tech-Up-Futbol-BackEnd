package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.service.PlayerSearchServiceImpl;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PlayerSearchServiceImpl Tests")
class PlayerSearchServiceImplTest {

    @InjectMocks
    private PlayerSearchServiceImpl service;

    @Mock
    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        DataStore.limpiarDatos();
        when(playerRepository.findAll())
                .thenAnswer(inv -> new ArrayList<>(DataStore.jugadores.values()));
    }

    // ── Happy Path

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("HP-PSS-01: search() sin filtros retorna solo jugadores disponibles")
        void searchSinFiltrosSoloDisponibles() {
            registrar(buildStudent("a@escuelaing.edu.co", 1001, "Disponible A", 20, PositionEnum.Midfielder, false, 3));
            registrar(buildStudent("b@escuelaing.edu.co", 1002, "Con Equipo B",  21, PositionEnum.Defender,   true,  4));

            List<Player> result = service.search(null, null, null, null, null, null, null);

            assertEquals(1, result.size());
            assertEquals("Disponible A", result.get(0).getFullname());
            assertFalse(result.get(0).isHaveTeam());
        }

        @Test
        @DisplayName("HP-PSS-02: search() filtra por posición correctamente")
        void searchFiltraPorPosicion() {
            registrar(buildStudent("c@escuelaing.edu.co", 1003, "Portero",    20, PositionEnum.GoalKeeper, false, 3));
            registrar(buildStudent("d@escuelaing.edu.co", 1004, "Defensa",    21, PositionEnum.Defender,   false, 4));

            List<Player> result = service.search(PositionEnum.GoalKeeper, null, null, null, null, null, null);

            assertEquals(1, result.size());
            assertEquals(PositionEnum.GoalKeeper, result.get(0).getPosition());
        }

        @Test
        @DisplayName("HP-PSS-03: search() filtra por género correctamente")
        void searchFiltraPorGenero() {
            registrar(buildStudentConGenero("e@escuelaing.edu.co", 1005, "Masculino Uno", false, "Masculino"));
            registrar(buildStudentConGenero("f@escuelaing.edu.co", 1006, "Femenino Uno",  false, "Femenino"));

            List<Player> result = service.search(null, null, null, null, "Femenino", null, null);

            assertEquals(1, result.size());
            assertEquals("Femenino", result.get(0).getGender());
        }

        @Test
        @DisplayName("HP-PSS-04: search() filtra por edad mínima")
        void searchFiltraPorEdadMinima() {
            registrar(buildStudent("g@escuelaing.edu.co", 1007, "Joven 18", 18, PositionEnum.Winger, false, 2));
            registrar(buildStudent("h@escuelaing.edu.co", 1008, "Mayor 25", 25, PositionEnum.Winger, false, 6));

            List<Player> result = service.search(null, null, 20, null, null, null, null);

            assertEquals(1, result.size());
            assertEquals("Mayor 25", result.get(0).getFullname());
        }

        @Test
        @DisplayName("HP-PSS-05: search() filtra por edad máxima")
        void searchFiltraPorEdadMaxima() {
            registrar(buildStudent("i@escuelaing.edu.co", 1009, "Joven 18", 18, PositionEnum.Winger, false, 2));
            registrar(buildStudent("j@escuelaing.edu.co", 1010, "Mayor 30", 30, PositionEnum.Winger, false, 6));

            List<Player> result = service.search(null, null, null, 25, null, null, null);

            assertEquals(1, result.size());
            assertEquals("Joven 18", result.get(0).getFullname());
        }

        @Test
        @DisplayName("HP-PSS-06: search() filtra por nombre (case-insensitive, parcial)")
        void searchFiltraPorNombre() {
            registrar(buildStudent("k@escuelaing.edu.co", 1011, "Carlos Andrés",  22, PositionEnum.Midfielder, false, 4));
            registrar(buildStudent("l@escuelaing.edu.co", 1012, "Pedro González", 23, PositionEnum.Defender,   false, 5));

            List<Player> result = service.search(null, null, null, null, null, "carlos", null);

            assertEquals(1, result.size());
            assertTrue(result.get(0).getFullname().toLowerCase().contains("carlos"));
        }

        @Test
        @DisplayName("HP-PSS-07: search() filtra por semestre (solo StudentPlayer)")
        void searchFiltraPorSemestre() {
            registrar(buildStudent("m@escuelaing.edu.co", 1013, "Sem 3", 20, PositionEnum.Defender, false, 3));
            registrar(buildStudent("n@escuelaing.edu.co", 1014, "Sem 7", 22, PositionEnum.Defender, false, 7));

            List<Player> result = service.search(null, 3, null, null, null, null, null);

            assertEquals(1, result.size());
            assertTrue(result.get(0) instanceof StudentPlayer s && s.getSemester() == 3);
        }

        @Test
        @DisplayName("HP-PSS-08: search() por numberID retorna el jugador exacto")
        void searchPorNumberId() {
            registrar(buildStudent("o@escuelaing.edu.co", 1015, "ID Exacto", 20, PositionEnum.Midfielder, false, 4));
            registrar(buildStudent("p@escuelaing.edu.co", 1016, "ID Otro",   21, PositionEnum.Midfielder, false, 4));

            List<Player> result = service.search(null, null, null, null, null, null, 1015);

            assertEquals(1, result.size());
            assertEquals("ID Exacto", result.get(0).getFullname());
        }

        @Test
        @DisplayName("HP-PSS-09: search() retorna lista vacía si nadie cumple los filtros")
        void searchRetornaVacioSiNadieCoincide() {
            registrar(buildStudent("q@escuelaing.edu.co", 1017, "No Coincide", 20, PositionEnum.Defender, false, 3));

            List<Player> result = service.search(PositionEnum.GoalKeeper, null, null, null, null, null, null);

            assertTrue(result.isEmpty());
        }
    }

    // ── Error Path

    @Nested
    @DisplayName("Error Path")
    class ErrorPath {

        @Test
        @DisplayName("EP-PSS-01: search() con DataStore vacío retorna lista vacía sin excepción")
        void searchDataStoreVacioRetornaVacio() {
            assertDoesNotThrow(() -> {
                List<Player> result = service.search(null, null, null, null, null, null, null);
                assertTrue(result.isEmpty());
            });
        }

        @Test
        @DisplayName("EP-PSS-02: search() no retorna jugadores con haveTeam=true aunque coincidan filtros")
        void searchNoRetornaJugadoresConEquipo() {
            registrar(buildStudent("r@escuelaing.edu.co", 1018, "Con Equipo", 22, PositionEnum.Midfielder, true, 4));

            List<Player> result = service.search(null, null, null, null, null, null, null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("EP-PSS-03: search() filtra por semestre solo StudentPlayer — InstitutionalPlayer ignorado")
        void searchSemestreIgnoraNoEstudiantes() {
            InstitutionalPlayer inst = new InstitutionalPlayer();
            inst.setId(UUID.randomUUID().toString());
            inst.setFullname("Institucional");
            inst.setEmail("inst@gmail.com");
            inst.setNumberID(9999);
            inst.setAge(30);
            inst.setGender("Masculino");
            inst.setHaveTeam(false);
            inst.setPosition(PositionEnum.Defender);
            DataStore.jugadores.put(inst.getId(), inst);

            List<Player> result = service.search(null, 3, null, null, null, null, null);
            assertTrue(result.isEmpty());
        }
    }

    // ── Conditional Scenarios

    @Nested
    @DisplayName("Conditional Scenarios")
    class ConditionalScenarios {

        @Test
        @DisplayName("CS-PSS-01: search() con múltiples filtros aplica todos combinados (AND)")
        void searchMultiplesFiltrosAND() {
            registrar(buildStudent("s@escuelaing.edu.co", 1019, "Match All",    22, PositionEnum.Midfielder, false, 4));
            registrar(buildStudent("t@escuelaing.edu.co", 1020, "Wrong Age",    30, PositionEnum.Midfielder, false, 4));
            registrar(buildStudent("u@escuelaing.edu.co", 1021, "Wrong Pos",    22, PositionEnum.Defender,   false, 4));

            List<Player> result = service.search(PositionEnum.Midfielder, null, 20, 25, null, null, null);

            assertEquals(1, result.size());
            assertEquals("Match All", result.get(0).getFullname());
        }

        @Test
        @DisplayName("CS-PSS-02: search() por nombre es case-insensitive")
        void searchNombreCaseInsensitive() {
            registrar(buildStudent("v@escuelaing.edu.co", 1022, "Andrés López", 20, PositionEnum.Defender, false, 3));

            List<Player> result = service.search(null, null, null, null, null, "ANDRÉS", null);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("CS-PSS-03: search() por género es case-insensitive")
        void searchGeneroCaseInsensitive() {
            registrar(buildStudentConGenero("w@escuelaing.edu.co", 1023, "Género Test", false, "Masculino"));

            List<Player> result = service.search(null, null, null, null, "masculino", null, null);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("CS-PSS-04: search() retorna StudentPlayer correctamente")
        void searchRetornaTipoStudent() {
            registrar(buildStudent("x@escuelaing.edu.co", 1024, "Estudiante", 20, PositionEnum.Midfielder, false, 3));
            List<Player> result = service.search(null, null, null, null, null, null, null);
            assertTrue(result.get(0) instanceof StudentPlayer);
        }

        @Test
        @DisplayName("CS-PSS-05: search() retorna todos disponibles cuando hay mezcla disponible/no disponible")
        void searchMezclaSoloDisponibles() {
            for (int i = 0; i < 3; i++) {
                registrar(buildStudent("avail" + i + "@escuelaing.edu.co", 2000 + i,
                        "Avail " + i, 20, PositionEnum.Defender, false, 3));
                registrar(buildStudent("busy" + i + "@escuelaing.edu.co", 3000 + i,
                        "Busy " + i, 20, PositionEnum.Defender, true, 3));
            }
            List<Player> result = service.search(null, null, null, null, null, null, null);
            assertEquals(3, result.size());
            result.forEach(r -> assertFalse(r.isHaveTeam()));
        }
    }

    // ── Helpers

    private void registrar(Player p) {
        DataStore.jugadores.put(p.getId(), p);
    }

    private StudentPlayer buildStudent(String email, int numberId, String name, int age,
                                       PositionEnum pos, boolean haveTeam, int semester) {
        StudentPlayer p = new StudentPlayer();
        p.setId(UUID.randomUUID().toString());
        p.setFullname(name);
        p.setEmail(email);
        p.setNumberID(numberId);
        p.setAge(age);
        p.setGender("Masculino");
        p.setPosition(pos);
        p.setHaveTeam(haveTeam);
        p.setSemester(semester);
        p.setDorsalNumber(10);
        return p;
    }

    private StudentPlayer buildStudentConGenero(String email, int numberId, String name,
                                                 boolean haveTeam, String genero) {
        StudentPlayer p = buildStudent(email, numberId, name, 22, PositionEnum.Defender, haveTeam, 4);
        p.setGender(genero);
        return p;
    }
}
