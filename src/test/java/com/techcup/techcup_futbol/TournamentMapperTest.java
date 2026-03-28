package com.techcup.techcup_futbol;
import com.techcup.techcup_futbol.core.model.TournamentState;
import com.techcup.techcup_futbol.core.model.Tournament;
import com.techcup.techcup_futbol.Controller.dto.TournamentDTO;
import com.techcup.techcup_futbol.Controller.mapper.TournamentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class TournamentMapperTest {

    private TournamentDTO dtoTorneo;
    private Tournament torneoCompleto;

    @BeforeEach
    void setup(){
        dtoTorneo = new TournamentDTO();
        dtoTorneo.setId("dto-torneo-001");
        dtoTorneo.setName("TechCup 2026-1");
        dtoTorneo.setStartDate(LocalDateTime.of(2026, 3, 18, 8, 0));
        dtoTorneo.setEndDate(LocalDateTime.of(2026, 4, 16, 16, 0));
        dtoTorneo.setRegistrationFee(130000.0);
        dtoTorneo.setMaxTeams(16);
        dtoTorneo.setRules("Reglas por hacer");
        dtoTorneo.setCurrentState(TournamentState.DRAFT);


        torneoCompleto = new Tournament();
        torneoCompleto.setId("T003");
        torneoCompleto.setName("TechCup Relampago 2026-1");
        torneoCompleto.setStartDate(LocalDateTime.of(2026, 4, 18, 8, 00));
        torneoCompleto.setEndDate(LocalDateTime.of(2026, 5, 16, 17, 00));
        torneoCompleto.setRegistrationFee(130000.0);
        torneoCompleto.setMaxTeams(16);
        torneoCompleto.setRules("Reglas oficiales");
        torneoCompleto.setCurrentState(TournamentState.ACTIVE);

    }

    @Test
    void testToModel_DTOTorneo_RetornaTorneo(){
        Tournament resultado = TournamentMapper.toModel(dtoTorneo);
        assertNotNull(resultado);
        assertInstanceOf(Tournament.class, resultado);
        assertEquals(dtoTorneo.getId(), resultado.getId());
        assertEquals(dtoTorneo.getName(), resultado.getName());
        assertEquals(dtoTorneo.getStartDate(), resultado.getStartDate());
        assertEquals(dtoTorneo.getEndDate(), resultado.getEndDate());
        assertEquals(dtoTorneo.getRegistrationFee(), resultado.getRegistrationFee());
        assertEquals(dtoTorneo.getMaxTeams(), resultado.getMaxTeams());
        assertEquals(dtoTorneo.getRules(), resultado.getRules());
        assertEquals(dtoTorneo.getCurrentState(), resultado.getCurrentState());
    }

    @Test
    void testToDTO_Torneo_RetornaTorneoCompleto(){
        TournamentDTO resultado = TournamentMapper.toDTO(torneoCompleto);
        assertNotNull(resultado);
        assertInstanceOf(TournamentDTO.class, resultado);
        assertEquals(torneoCompleto.getId(), resultado.getId());
        assertEquals(torneoCompleto.getName(), resultado.getName());
        assertEquals(torneoCompleto.getStartDate(), resultado.getStartDate());
        assertEquals(torneoCompleto.getEndDate(), resultado.getEndDate());
        assertEquals(torneoCompleto.getRegistrationFee(), resultado.getRegistrationFee());
        assertEquals(torneoCompleto.getMaxTeams(), resultado.getMaxTeams());
        assertEquals(torneoCompleto.getRules(), resultado.getRules());
        assertEquals(torneoCompleto.getCurrentState(), resultado.getCurrentState());
    }
    @Test
    void testToModel_ToDTO_RoundTrip_MantieneDatos(){
        TournamentDTO dto = TournamentMapper.toDTO(torneoCompleto);
        Tournament torneoConvertido = TournamentMapper.toModel(dto);

        assertNotNull(torneoConvertido);
        assertInstanceOf(Tournament.class, torneoConvertido);
        assertEquals(torneoCompleto.getId(), torneoConvertido.getId());
        assertEquals(torneoCompleto.getName(), torneoConvertido.getName());
        assertEquals(torneoCompleto.getStartDate(), torneoConvertido.getStartDate());
        assertEquals(torneoCompleto.getEndDate(), torneoConvertido.getEndDate());
        assertEquals(torneoCompleto.getRegistrationFee(), torneoConvertido.getRegistrationFee());
        assertEquals(torneoCompleto.getMaxTeams(), torneoConvertido.getMaxTeams());
        assertEquals(torneoCompleto.getRules(), torneoConvertido.getRules());
        assertEquals(torneoCompleto.getCurrentState(), torneoConvertido.getCurrentState());
    }

    //ERROR PATH

    @Test
    void testToModel_Null_ReturnNull(){
        Tournament resultado = TournamentMapper.toModel(null);
        assertNull(resultado);
    }

    @Test
    void testToDTO_Null_ReturnNull(){
        TournamentDTO resultado = TournamentMapper.toDTO(null);
        assertNull(resultado);
    }

}
