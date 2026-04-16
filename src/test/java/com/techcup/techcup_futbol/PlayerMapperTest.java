package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.controller.dto.PlayerDTO;
import com.techcup.techcup_futbol.controller.dto.PlayerResponse;
import com.techcup.techcup_futbol.controller.mapper.PlayerMapper;
import com.techcup.techcup_futbol.core.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerMapperTest {

    private PlayerDTO buildDto(String type) {
        PlayerDTO dto = new PlayerDTO();
        dto.setFullname("Test Player");
        dto.setEmail("test@example.com");
        dto.setNumberID(123456);
        dto.setAge(22);
        dto.setGender("M");
        dto.setCaptain(false);
        dto.setPosition(PositionEnum.Midfielder);
        dto.setDorsalNumber(10);
        dto.setPlayerType(type);
        return dto;
    }

    // ── toModel ──

    @Test
    void toModel_nullDto_returnsNull() {
        assertNull(PlayerMapper.toModel(null));
    }

    @Test
    void toModel_studentType_returnsStudentPlayer() {
        PlayerDTO dto = buildDto("STUDENT");
        dto.setSemester(5);

        Player result = PlayerMapper.toModel(dto);

        assertInstanceOf(StudentPlayer.class, result);
        assertEquals(5, ((StudentPlayer) result).getSemester());
        assertEquals("Test Player", result.getFullname());
    }

    @Test
    void toModel_studentType_nullSemester_defaults0() {
        PlayerDTO dto = buildDto("STUDENT");
        dto.setSemester(null);

        Player result = PlayerMapper.toModel(dto);

        assertInstanceOf(StudentPlayer.class, result);
        assertEquals(0, ((StudentPlayer) result).getSemester());
    }

    @Test
    void toModel_institutionalType_returnsInstitutionalPlayer() {
        PlayerDTO dto = buildDto("INSTITUTIONAL");

        Player result = PlayerMapper.toModel(dto);

        assertInstanceOf(InstitutionalPlayer.class, result);
        assertEquals("Test Player", result.getFullname());
    }

    @Test
    void toModel_externalType_returnsExternalPlayer() {
        PlayerDTO dto = buildDto("EXTERNAL");
        dto.setRelationship("Familiar");

        Player result = PlayerMapper.toModel(dto);

        assertInstanceOf(ExternalPlayer.class, result);
        assertEquals("Familiar", ((ExternalPlayer) result).getRelationship());
    }

    @Test
    void toModel_externalType_nullRelationship_setsNull() {
        PlayerDTO dto = buildDto("EXTERNAL");
        dto.setRelationship(null);

        Player result = PlayerMapper.toModel(dto);

        assertInstanceOf(ExternalPlayer.class, result);
        assertNull(((ExternalPlayer) result).getRelationship());
    }

    @Test
    void toModel_unknownType_throwsException() {
        PlayerDTO dto = buildDto("UNKNOWN");

        assertThrows(IllegalArgumentException.class, () -> PlayerMapper.toModel(dto));
    }

    @Test
    void toModel_withPassword_setsPasswordHash() {
        PlayerDTO dto = buildDto("INSTITUTIONAL");
        dto.setPassword("SecurePass123");

        Player result = PlayerMapper.toModel(dto);

        assertEquals("SecurePass123", result.getPasswordHash());
    }

    @Test
    void toModel_blankPassword_doesNotSetPasswordHash() {
        PlayerDTO dto = buildDto("INSTITUTIONAL");
        dto.setPassword("   ");

        Player result = PlayerMapper.toModel(dto);

        assertNull(result.getPasswordHash());
    }

    // ── toDTO ──

    @Test
    void toDTO_nullPlayer_returnsNull() {
        assertNull(PlayerMapper.toDTO(null));
    }

    @Test
    void toDTO_studentPlayer_setsTypeAndSemester() {
        StudentPlayer student = new StudentPlayer();
        student.setId("p1");
        student.setFullname("Juan");
        student.setEmail("juan@example.com");
        student.setPosition(PositionEnum.Winger);
        student.setSemester(7);

        PlayerDTO result = PlayerMapper.toDTO(student);

        assertEquals("STUDENT", result.getPlayerType());
        assertEquals(7, result.getSemester());
        assertEquals("Juan", result.getFullname());
    }

    @Test
    void toDTO_institutionalPlayer_setsType() {
        InstitutionalPlayer institutional = new InstitutionalPlayer();
        institutional.setId("p2");
        institutional.setFullname("Maria");

        PlayerDTO result = PlayerMapper.toDTO(institutional);

        assertEquals("INSTITUTIONAL", result.getPlayerType());
    }

    @Test
    void toDTO_externalPlayer_setsType() {
        ExternalPlayer external = new ExternalPlayer();
        external.setId("p3");
        external.setFullname("Pedro");

        PlayerDTO result = PlayerMapper.toDTO(external);

        assertEquals("EXTERNAL", result.getPlayerType());
    }

    // ── mapToResponse ──

    @Test
    void mapToResponse_nullPlayer_returnsNull() {
        assertNull(PlayerMapper.mapToResponse(null));
    }

    @Test
    void mapToResponse_studentPlayer_includesSemester() {
        StudentPlayer student = new StudentPlayer();
        student.setId("p1");
        student.setFullname("Carlos");
        student.setPosition(PositionEnum.GoalKeeper);
        student.setSemester(4);

        PlayerResponse response = PlayerMapper.mapToResponse(student);

        assertEquals("p1", response.id());
        assertEquals("Carlos", response.fullname());
        assertEquals(4, response.semester());
    }

    @Test
    void mapToResponse_institutionalPlayer_nullSemester() {
        InstitutionalPlayer inst = new InstitutionalPlayer();
        inst.setId("p2");
        inst.setFullname("Ana");

        PlayerResponse response = PlayerMapper.mapToResponse(inst);

        assertNull(response.semester());
    }
}
