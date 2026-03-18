package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.Controller.dto.PlayerDTO;
import com.techcup.techcup_futbol.Controller.mapper.PlayerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Player Mapper Tests")
class PlayerMapperTest {

    private PlayerDTO dtoEstudiante;
    private PlayerDTO dtoInstitucional;
    private PlayerDTO dtoFamiliar;
    private Player playerCompleto;

    @BeforeEach
    void setUp() {
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

    @Test
    @DisplayName("HP-M01: DTO STUDENT → StudentPlayer con todos los campos")
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
    @DisplayName("HP-M02: DTO INSTITUTIONAL → InstitutionalPlayer")
    void testToModel_DTOInstitucional_RetornaInstitutionalPlayer() {
        Player resultado = PlayerMapper.toModel(dtoInstitucional);

        assertNotNull(resultado);
        assertInstanceOf(InstitutionalPlayer.class, resultado);
        assertEquals(dtoInstitucional.getFullname(), resultado.getFullname());
    }

    @Test
    @DisplayName("HP-M03: DTO RELATIVE → RelativePlayer")
    void testToModel_DTOFamiliar_RetornaRelativePlayer() {
        Player resultado = PlayerMapper.toModel(dtoFamiliar);

        assertNotNull(resultado);
        assertInstanceOf(RelativePlayer.class, resultado);
        assertEquals(dtoFamiliar.getFullname(), resultado.getFullname());
    }

    @Test
    @DisplayName("HP-M04: StudentPlayer → DTO con tipo STUDENT y semestre")
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
    @DisplayName("HP-M05: InstitutionalPlayer → DTO con tipo INSTITUTIONAL")
    void testToDTO_InstitutionalPlayer_RetornaDTOConTipo() {
        InstitutionalPlayer inst = new InstitutionalPlayer();
        inst.setId("J002");
        inst.setFullname("Pedro Sánchez");
        inst.setEmail("pedro@gmail.com");

        PlayerDTO resultado = PlayerMapper.toDTO(inst);

        assertNotNull(resultado);
        assertEquals("INSTITUTIONAL", resultado.getPlayerType());
    }

    @Test
    @DisplayName("HP-M06: RelativePlayer → DTO con tipo RELATIVE")
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
    @DisplayName("HP-M07: Round-trip toModel → toDTO conserva datos")
    void testRoundTrip_ConservaDatos() {
        PlayerDTO dto = PlayerMapper.toDTO(playerCompleto);
        Player convertido = PlayerMapper.toModel(dto);

        assertNotNull(convertido);
        assertInstanceOf(StudentPlayer.class, convertido);
        assertEquals(playerCompleto.getFullname(), convertido.getFullname());
        assertEquals(playerCompleto.getEmail(), convertido.getEmail());
        assertEquals(playerCompleto.getPosition(), convertido.getPosition());
        assertEquals(playerCompleto.getDorsalNumber(), convertido.getDorsalNumber());
        assertEquals(playerCompleto.getAge(), convertido.getAge());
        assertEquals(playerCompleto.getGender(), convertido.getGender());
        assertEquals(playerCompleto.isCaptain(), convertido.isCaptain());
    }

    @Test
    @DisplayName("EP-M01: DTO null → retorna null")
    void testToModel_DTONull_RetornaNull() {
        assertNull(PlayerMapper.toModel(null));
    }

    @Test
    @DisplayName("EP-M02: Player null → retorna null")
    void testToDTO_PlayerNull_RetornaNull() {
        assertNull(PlayerMapper.toDTO(null));
    }

    @Test
    @DisplayName("EP-M03: playerType inválido lanza IllegalArgumentException")
    void testToModel_TipoInvalido_LanzaExcepcion() {
        dtoEstudiante.setPlayerType("TIPO_INVALIDO");

        assertThrows(IllegalArgumentException.class, () ->
                PlayerMapper.toModel(dtoEstudiante)
        );
    }

    @Test
    @DisplayName("CS-M01: Campos opcionales null se manejan sin error")
    void testToModel_CamposOpcionalesNull() {
        dtoEstudiante.setPhotoUrl(null);
        dtoEstudiante.setNumberID(0);

        Player resultado = PlayerMapper.toModel(dtoEstudiante);

        assertNotNull(resultado);
        assertNull(resultado.getPhotoUrl());
        assertEquals(0, resultado.getNumberID());
    }
}