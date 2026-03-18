package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.Controller.dto.PlayerDTO;
import com.techcup.techcup_futbol.Controller.mapper.PlayerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerMapperTest {

    private PlayerDTO dtoEstudiante;
    private PlayerDTO dtoInstitucional;
    private PlayerDTO dtoFamiliar;
    private Player playerCompleto;

    @BeforeEach
    void setUp() {

        // DTO base para StudentPlayer
        dtoEstudiante = new PlayerDTO();
        dtoEstudiante.setId("J001");
        dtoEstudiante.setFullname("Carlos Rodríguez");
        dtoEstudiante.setEmail("carlos@escuelaing.edu.co");
        dtoEstudiante.setNumberID(123456);
        dtoEstudiante.setPosition(PositionEnum.Defender);
        dtoEstudiante.setDorsalNumber(4);
        dtoEstudiante.setPhotoUrl("carlos.jpg");
        dtoEstudiante.setHaveTeam(true);
        dtoEstudiante.setAge(22);
        dtoEstudiante.setGender("Masculino");
        dtoEstudiante.setCaptain(true);
        dtoEstudiante.setPlayerType("STUDENT");
        dtoEstudiante.setSemester(6);

        // DTO para InstitutionalPlayer
        dtoInstitucional = new PlayerDTO();
        dtoInstitucional.setId("J002");
        dtoInstitucional.setFullname("Pedro Sánchez");
        dtoInstitucional.setEmail("pedro@gmail.com");
        dtoInstitucional.setNumberID(123458);
        dtoInstitucional.setPosition(PositionEnum.Midfielder);
        dtoInstitucional.setDorsalNumber(8);
        dtoInstitucional.setAge(23);
        dtoInstitucional.setGender("Masculino");
        dtoInstitucional.setPlayerType("INSTITUTIONAL");

        // DTO para RelativePlayer
        dtoFamiliar = new PlayerDTO();
        dtoFamiliar.setId("J003");
        dtoFamiliar.setFullname("Laura Torres");
        dtoFamiliar.setEmail("laura@gmail.com");
        dtoFamiliar.setNumberID(123462);
        dtoFamiliar.setPosition(PositionEnum.Midfielder);
        dtoFamiliar.setDorsalNumber(6);
        dtoFamiliar.setAge(22);
        dtoFamiliar.setGender("Femenino");
        dtoFamiliar.setPlayerType("RELATIVE");

        // Player modelo para toDTO()
        playerCompleto = new StudentPlayer();
        playerCompleto.setId("J001");
        playerCompleto.setFullname("Carlos Rodríguez");
        playerCompleto.setEmail("carlos@escuelaing.edu.co");
        playerCompleto.setNumberID(123456);
        playerCompleto.setPosition(PositionEnum.Defender);
        playerCompleto.setDorsalNumber(4);
        playerCompleto.setPhotoUrl("carlos.jpg");
        playerCompleto.setHaveTeam(true);
        playerCompleto.setAge(22);
        playerCompleto.setGender("Masculino");
        playerCompleto.setCaptain(true);
        ((StudentPlayer) playerCompleto).setSemester(6);
    }

    // HAPPY PATH TESTS

    @Test
    void testToModel_DTOEstudiante_RetornaStudentPlayer() {
        Player resultado = PlayerMapper.toModel(dtoEstudiante);

        assertNotNull(resultado);
        assertInstanceOf(StudentPlayer.class, resultado);
        assertEquals(dtoEstudiante.getFullname(), resultado.getFullname());
        assertEquals(dtoEstudiante.getEmail(), resultado.getEmail());
        assertEquals(dtoEstudiante.getPosition(), resultado.getPosition());
        assertEquals(dtoEstudiante.getDorsalNumber(), resultado.getDorsalNumber());
        assertEquals(dtoEstudiante.getPhotoUrl(), resultado.getPhotoUrl());
        assertEquals(dtoEstudiante.getAge(), resultado.getAge());
        assertEquals(dtoEstudiante.getGender(), resultado.getGender());
        assertEquals(dtoEstudiante.isCaptain(), resultado.isCaptain());
        assertEquals(dtoEstudiante.getSemester(), ((StudentPlayer) resultado).getSemester());
    }

    @Test
    void testToModel_DTOInstitucional_RetornaInstitutionalPlayer() {
        Player resultado = PlayerMapper.toModel(dtoInstitucional);

        assertNotNull(resultado);
        assertInstanceOf(InstitutionalPlayer.class, resultado);
        assertEquals(dtoInstitucional.getFullname(), resultado.getFullname());
    }

    @Test
    void testToModel_DTOFamiliar_RetornaRelativePlayer() {
        Player resultado = PlayerMapper.toModel(dtoFamiliar);

        assertNotNull(resultado);
        assertInstanceOf(RelativePlayer.class, resultado);
        assertEquals(dtoFamiliar.getFullname(), resultado.getFullname());
    }

    @Test
    void testToDTO_StudentPlayer_RetornaDTOConTipoYSemestre() {
        PlayerDTO resultado = PlayerMapper.toDTO(playerCompleto);

        assertNotNull(resultado);
        assertEquals("STUDENT", resultado.getPlayerType());
        assertEquals(6, resultado.getSemester());
        assertEquals(playerCompleto.getId(), resultado.getId());
        assertEquals(playerCompleto.getFullname(), resultado.getFullname());
        assertEquals(playerCompleto.getEmail(), resultado.getEmail());
        assertEquals(playerCompleto.getPosition(), resultado.getPosition());
        assertEquals(playerCompleto.getDorsalNumber(), resultado.getDorsalNumber());
        assertEquals(playerCompleto.isHaveTeam(), resultado.isHaveTeam());
        assertEquals(playerCompleto.isCaptain(), resultado.isCaptain());
    }

    @Test
    void testToDTO_InstitutionalPlayer_RetornaDTOConTipo() {
        InstitutionalPlayer institucional = new InstitutionalPlayer();
        institucional.setId("J002");
        institucional.setFullname("Pedro Sánchez");
        institucional.setEmail("pedro@gmail.com");

        PlayerDTO resultado = PlayerMapper.toDTO(institucional);

        assertNotNull(resultado);
        assertEquals("INSTITUTIONAL", resultado.getPlayerType());
    }

    @Test
    void testToDTO_RelativePlayer_RetornaDTOConTipo() {
        RelativePlayer familiar = new RelativePlayer();
        familiar.setId("J003");
        familiar.setFullname("Laura Torres");
        familiar.setEmail("laura@gmail.com");

        PlayerDTO resultado = PlayerMapper.toDTO(familiar);

        assertNotNull(resultado);
        assertEquals("RELATIVE", resultado.getPlayerType());
    }

    @Test
    void testToModel_ToDTO_RoundTrip_ConservaDatos() {
        PlayerDTO dto = PlayerMapper.toDTO(playerCompleto);
        Player modeloConvertido = PlayerMapper.toModel(dto);

        assertNotNull(modeloConvertido);
        assertInstanceOf(StudentPlayer.class, modeloConvertido);
        assertEquals(playerCompleto.getFullname(), modeloConvertido.getFullname());
        assertEquals(playerCompleto.getEmail(), modeloConvertido.getEmail());
        assertEquals(playerCompleto.getPosition(), modeloConvertido.getPosition());
        assertEquals(playerCompleto.getDorsalNumber(), modeloConvertido.getDorsalNumber());
        assertEquals(playerCompleto.getAge(), modeloConvertido.getAge());
        assertEquals(playerCompleto.getGender(), modeloConvertido.getGender());
        assertEquals(playerCompleto.isCaptain(), modeloConvertido.isCaptain());
    }

    // ERROR PATH TESTS

    @Test
    void testToModel_DTONull_RetornaNull() {
        Player resultado = PlayerMapper.toModel(null);
        assertNull(resultado);
    }

    @Test
    void testToDTO_PlayerNull_RetornaNull() {
        PlayerDTO resultado = PlayerMapper.toDTO(null);
        assertNull(resultado);
    }

    @Test
    void testToModel_PlayerTypeTipoInvalido_LanzaExcepcion() {
        dtoEstudiante.setPlayerType("TIPO_INVALIDO");

        assertThrows(IllegalArgumentException.class, () -> {
            PlayerMapper.toModel(dtoEstudiante);
        });
    }

    @Test
    void testToModel_CamposOpcionalesNull_ManejaCorrectamente() {
        dtoEstudiante.setPhotoUrl(null);
        dtoEstudiante.setNumberID(0);

        Player resultado = PlayerMapper.toModel(dtoEstudiante);

        assertNotNull(resultado);
        assertNull(resultado.getPhotoUrl());
        assertEquals(0, resultado.getNumberID());
    }
}