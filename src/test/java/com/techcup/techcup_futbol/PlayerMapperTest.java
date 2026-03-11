package com.techcup.techcup_futbol;
import com.techcup.techcup_futbol.model.Player;
import com.techcup.techcup_futbol.model.PositionEnum;
import com.techcup.techcup_futbol.model.StudentPlayer;
import com.techcup.techcup_futbol.util.PlayerDTO;
import com.techcup.techcup_futbol.util.PlayerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerMapperTest {

    private PlayerDTO dtoCompleto;
    private Player playerCompleto;

    @BeforeEach
    void setUp() {

        dtoCompleto = new PlayerDTO();
        dtoCompleto.setId("J001");
        dtoCompleto.setFullname("Carlos Rodríguez");
        dtoCompleto.setEmail("carlos@escuelaing.edu.co");
        dtoCompleto.setNumberID(123456);
        dtoCompleto.setPosition(PositionEnum.Defender);
        dtoCompleto.setDorsalNumber(4);
        dtoCompleto.setPhotoUrl("carlos.jpg");
        dtoCompleto.setHaveTeam(true);
        dtoCompleto.setAge(22);
        dtoCompleto.setGender("Masculino");
        dtoCompleto.setCaptain(true);


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
    }

    // HAPPY PATH TESTS

    @Test
    void testToModel_DTOCompleto_RetornaPlayerValido() {

        Player resultado = PlayerMapper.toModel(dtoCompleto);

        assertNotNull(resultado);
        assertEquals(dtoCompleto.getFullname(), resultado.getFullname());
        assertEquals(dtoCompleto.getEmail(), resultado.getEmail());
        assertEquals(dtoCompleto.getPosition(), resultado.getPosition());
        assertEquals(dtoCompleto.getDorsalNumber(), resultado.getDorsalNumber());
        assertEquals(dtoCompleto.getPhotoUrl(), resultado.getPhotoUrl());
        assertEquals(dtoCompleto.getAge(), resultado.getAge());
        assertEquals(dtoCompleto.getGender(), resultado.getGender());
        assertEquals(dtoCompleto.isCaptain(), resultado.isCaptain());
    }

    @Test
    void testToDTO_PlayerCompleto_RetornaDTOValido() {

        PlayerDTO resultado = PlayerMapper.toDTO(playerCompleto);


        assertNotNull(resultado);
        assertEquals(playerCompleto.getId(), resultado.getId());
        assertEquals(playerCompleto.getFullname(), resultado.getFullname());
        assertEquals(playerCompleto.getEmail(), resultado.getEmail());
        assertEquals(playerCompleto.getPosition(), resultado.getPosition());
        assertEquals(playerCompleto.getDorsalNumber(), resultado.getDorsalNumber());
        assertEquals(playerCompleto.getPhotoUrl(), resultado.getPhotoUrl());
        assertEquals(playerCompleto.isHaveTeam(), resultado.isHaveTeam());
        assertEquals(playerCompleto.getAge(), resultado.getAge());
        assertEquals(playerCompleto.getGender(), resultado.getGender());
        assertEquals(playerCompleto.isCaptain(), resultado.isCaptain());
    }

    @Test
    void testToModel_ToDTO_RoundTrip_ConservaDatos() {

        Player modeloOriginal = playerCompleto;
        PlayerDTO dto = PlayerMapper.toDTO(modeloOriginal);
        Player modeloConvertido = PlayerMapper.toModel(dto);


        assertNotNull(modeloConvertido);
        assertEquals(modeloOriginal.getFullname(), modeloConvertido.getFullname());
        assertEquals(modeloOriginal.getEmail(), modeloConvertido.getEmail());
        assertEquals(modeloOriginal.getPosition(), modeloConvertido.getPosition());
        assertEquals(modeloOriginal.getDorsalNumber(), modeloConvertido.getDorsalNumber());
        assertEquals(modeloOriginal.getPhotoUrl(), modeloConvertido.getPhotoUrl());
        assertEquals(modeloOriginal.getAge(), modeloConvertido.getAge());
        assertEquals(modeloOriginal.getGender(), modeloConvertido.getGender());
        assertEquals(modeloOriginal.isCaptain(), modeloConvertido.isCaptain());
    }

    // ERROR PATH TESTS }

    @Test
    void testToModel_DTONull_RetornaPlayerConCamposNull() {

        Player resultado = PlayerMapper.toModel(null);


        assertNull(resultado);
    }

    @Test
    void testToDTO_PlayerNull_RetornaDTOConCamposNull() {

        PlayerDTO resultado = PlayerMapper.toDTO(null);


        assertNull(resultado);
    }

    @Test
    void testToModel_DTOCamposOpcionalesNull_ManejaCorrectamente() {

        dtoCompleto.setPhotoUrl(null);
        dtoCompleto.setNumberID(0);


        Player resultado = PlayerMapper.toModel(dtoCompleto);


        assertNotNull(resultado);
        assertNull(resultado.getPhotoUrl());
        assertEquals(0, resultado.getNumberID());
    }
}