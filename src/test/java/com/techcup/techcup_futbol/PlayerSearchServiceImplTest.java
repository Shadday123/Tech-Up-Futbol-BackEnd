package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.service.PlayerSearchServiceImpl;
import com.techcup.techcup_futbol.persistence.entity.PlayerEntity;
import com.techcup.techcup_futbol.persistence.entity.StudentPlayerEntity;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerSearchServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerSearchServiceImpl playerSearchService;

    private StudentPlayerEntity buildStudentEntity(String id, String name, PositionEnum pos,
                                                    int age, String gender, int numberID,
                                                    int semester, boolean haveTeam, boolean disponible) {
        StudentPlayerEntity e = new StudentPlayerEntity();
        e.setId(id);
        e.setFullname(name);
        e.setPosition(pos);
        e.setAge(age);
        e.setGender(gender);
        e.setNumberID(numberID);
        e.setSemester(semester);
        e.setHaveTeam(haveTeam);
        e.setDisponible(disponible);
        return e;
    }

    private List<PlayerEntity> singleStudent() {
        return List.of(buildStudentEntity("p1", "Carlos Gomez", PositionEnum.Midfielder,
                22, "M", 111111, 5, false, true));
    }

    @Test
    void search_noFilters_returnsAvailablePlayersWithoutTeam() {
        when(playerRepository.findAll()).thenReturn(singleStudent());

        List<Player> result = playerSearchService.search(null, null, null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Carlos Gomez", result.get(0).getFullname());
    }

    @Test
    void search_playerWithTeam_excluded() {
        StudentPlayerEntity withTeam = buildStudentEntity("p2", "Con Equipo", PositionEnum.Forward,
                20, "M", 222222, 3, true, true);
        when(playerRepository.findAll()).thenReturn(List.of(withTeam));

        List<Player> result = playerSearchService.search(null, null, null, null, null, null, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void search_playerNotDisponible_excluded() {
        StudentPlayerEntity noDisp = buildStudentEntity("p3", "No Disponible", PositionEnum.Forward,
                20, "M", 333333, 3, false, false);
        when(playerRepository.findAll()).thenReturn(List.of(noDisp));

        List<Player> result = playerSearchService.search(null, null, null, null, null, null, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void search_byPosition_filtersCorrectly() {
        StudentPlayerEntity midfielder = buildStudentEntity("p1", "MF", PositionEnum.Midfielder, 22, "M", 1, 5, false, true);
        StudentPlayerEntity forward = buildStudentEntity("p2", "FW", PositionEnum.Forward, 22, "M", 2, 5, false, true);
        when(playerRepository.findAll()).thenReturn(List.of(midfielder, forward));

        List<Player> result = playerSearchService.search(PositionEnum.Midfielder, null, null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals(PositionEnum.Midfielder, result.get(0).getPosition());
    }

    @Test
    void search_byGender_filtersCorrectly() {
        StudentPlayerEntity male = buildStudentEntity("p1", "Man", PositionEnum.Forward, 20, "M", 1, 5, false, true);
        StudentPlayerEntity female = buildStudentEntity("p2", "Woman", PositionEnum.Forward, 20, "F", 2, 5, false, true);
        when(playerRepository.findAll()).thenReturn(List.of(male, female));

        List<Player> result = playerSearchService.search(null, null, null, null, "F", null, null);

        assertEquals(1, result.size());
        assertEquals("Woman", result.get(0).getFullname());
    }

    @Test
    void search_byMinAge_filtersCorrectly() {
        StudentPlayerEntity young = buildStudentEntity("p1", "Young", PositionEnum.Forward, 18, "M", 1, 2, false, true);
        StudentPlayerEntity old = buildStudentEntity("p2", "Old", PositionEnum.Forward, 25, "M", 2, 5, false, true);
        when(playerRepository.findAll()).thenReturn(List.of(young, old));

        List<Player> result = playerSearchService.search(null, null, 20, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Old", result.get(0).getFullname());
    }

    @Test
    void search_byMaxAge_filtersCorrectly() {
        StudentPlayerEntity young = buildStudentEntity("p1", "Young", PositionEnum.Forward, 18, "M", 1, 2, false, true);
        StudentPlayerEntity old = buildStudentEntity("p2", "Old", PositionEnum.Forward, 25, "M", 2, 5, false, true);
        when(playerRepository.findAll()).thenReturn(List.of(young, old));

        List<Player> result = playerSearchService.search(null, null, null, 20, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Young", result.get(0).getFullname());
    }

    @Test
    void search_byName_caseInsensitive() {
        when(playerRepository.findAll()).thenReturn(singleStudent());

        List<Player> result = playerSearchService.search(null, null, null, null, null, "carlos", null);

        assertEquals(1, result.size());
    }

    @Test
    void search_byName_blank_ignoresFilter() {
        when(playerRepository.findAll()).thenReturn(singleStudent());

        List<Player> result = playerSearchService.search(null, null, null, null, null, "  ", null);

        assertEquals(1, result.size());
    }

    @Test
    void search_byNumberID_filtersCorrectly() {
        when(playerRepository.findAll()).thenReturn(singleStudent());

        List<Player> result = playerSearchService.search(null, null, null, null, null, null, 111111);

        assertEquals(1, result.size());
    }

    @Test
    void search_byNumberID_noMatch_returnsEmpty() {
        when(playerRepository.findAll()).thenReturn(singleStudent());

        List<Player> result = playerSearchService.search(null, null, null, null, null, null, 999999);

        assertTrue(result.isEmpty());
    }

    @Test
    void search_bySemester_filtersStudentPlayers() {
        when(playerRepository.findAll()).thenReturn(singleStudent());

        List<Player> result = playerSearchService.search(null, 5, null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals(5, ((StudentPlayer) result.get(0)).getSemester());
    }

    @Test
    void search_bySemester_noMatch_returnsEmpty() {
        when(playerRepository.findAll()).thenReturn(singleStudent());

        List<Player> result = playerSearchService.search(null, 8, null, null, null, null, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void search_byGender_blankString_ignoresFilter() {
        when(playerRepository.findAll()).thenReturn(singleStudent());

        List<Player> result = playerSearchService.search(null, null, null, null, "", null, null);

        assertEquals(1, result.size());
    }
}
