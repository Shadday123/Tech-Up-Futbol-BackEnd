package com.techcup.techcup_futbol;
import com.techcup.techcup_futbol.Controller.dto.TeamDTO;
import com.techcup.techcup_futbol.core.model.InstitutionalPlayer;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.Controller.mapper.TeamMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

class TeamMapperTest {
    private TeamDTO dtoEquipo;
    private Team equipoCompleto;

    @BeforeEach
    void setUp() {
        StudentPlayer captainDTO = new StudentPlayer();
        captainDTO.setFullname("Vanessa Torres");
        StudentPlayer p1 = new StudentPlayer();
        p1.setFullname("Juan Rodriguez");
        InstitutionalPlayer p2 = new InstitutionalPlayer();
        p2.setFullname("David Correa");
        dtoEquipo = new TeamDTO();
        dtoEquipo.setId("E001");
        dtoEquipo.setTeamName("Los Asgardianos");
        dtoEquipo.setShieldUrl("asgard.jpg");
        dtoEquipo.setUniformColors(Collections.singletonList("Negro"));
        dtoEquipo.setCaptain(captainDTO);
        dtoEquipo.setPlayers(List.of(captainDTO,p1,p2));

        StudentPlayer captain = new StudentPlayer();
        captain.setFullname("Vanessa Torres");
        StudentPlayer p3 = new StudentPlayer();
        p3.setFullname("Juan Melo");
        InstitutionalPlayer p4 = new InstitutionalPlayer();
        p4.setFullname("David Cajamarca");
        equipoCompleto = new Team();
        equipoCompleto.setId("E001");
        equipoCompleto.setTeamName("Los Asgardianos");
        equipoCompleto.setShieldUrl("asgard.jpg");
        equipoCompleto.setUniformColors(Collections.singletonList("Negro y Blanco"));
        equipoCompleto.setCaptain(captain);
        equipoCompleto.setPlayers(List.of(captain,p1,p2,p3,p4));
    }

    @Test
    void testToModel_DTOEquipo_RetornaEquipo(){
        Team resultado = TeamMapper.DTOtoModel(dtoEquipo);
        assertNotNull(resultado);
        assertInstanceOf(Team.class, resultado);
        assertEquals(dtoEquipo.getId(), resultado.getId());
        assertEquals(dtoEquipo.getTeamName(), resultado.getTeamName());
        assertEquals(dtoEquipo.getShieldUrl(), resultado.getShieldUrl());
        assertEquals(dtoEquipo.getUniformColors(), resultado.getUniformColors());

    }

    @Test
    void testToDTO_EquipoCompleto_RetornaEquipoCompleto(){
        TeamDTO resultado = TeamMapper.ModeltoDTo(equipoCompleto);
        assertNotNull(resultado);
        assertInstanceOf(TeamDTO.class, resultado);
        assertEquals(equipoCompleto.getId(), resultado.getId());
        assertEquals(equipoCompleto.getTeamName(), resultado.getTeamName());
        assertEquals(equipoCompleto.getShieldUrl(), resultado.getShieldUrl());
        assertEquals(equipoCompleto.getUniformColors(), resultado.getUniformColors());

    }
    @Test
    void testToModel_ToDTO_RoundTrip_MantieneDatos(){
        TeamDTO dto = TeamMapper.ModeltoDTo(equipoCompleto);
        Team equipoConvertido = TeamMapper.DTOtoModel(dto);
        assertNotNull(equipoConvertido);
        assertInstanceOf(Team.class, equipoConvertido);
        assertEquals(equipoCompleto.getId(), equipoConvertido.getId());
        assertEquals(equipoCompleto.getTeamName(), equipoConvertido.getTeamName());
        assertEquals(equipoCompleto.getShieldUrl(), equipoConvertido.getShieldUrl());
        assertEquals(equipoCompleto.getUniformColors(), equipoConvertido.getUniformColors());
    }

    //ERROR PATH

    @Test
    void testToModel_Null_ReturnNull(){
        Team resultado = TeamMapper.DTOtoModel(null);
        assertNull(resultado);
    }

    @Test
    void testToDTO_Null_ReturnNull(){
        TeamDTO resultado = TeamMapper.ModeltoDTo(null);
        assertNull(resultado);
    }

}