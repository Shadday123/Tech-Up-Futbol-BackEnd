package com.techcup.techcup_futbol;

import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.repository.PlayerRepository;
import com.techcup.techcup_futbol.repository.TeamRepository;
import com.techcup.techcup_futbol.repository.TournamentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;

    public DataInitializer(PlayerRepository playerRepository, TeamRepository teamRepository, TournamentRepository tournamentRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public void run(String... args) {
        inicializarJugadores();
        inicializarEquipos();
        inicializarTorneos();
    }

    private void inicializarJugadores() {
        StudentPlayer j1 = new StudentPlayer();
        j1.setId("J001");
        j1.setFullname("Carlos Rodríguez");
        j1.setEmail("carlos.rodriguez@escuelaing.edu.co");
        j1.setNumberID(123456);
        j1.setPosition(PositionEnum.Defender);
        j1.setDorsalNumber(4);
        j1.setPhotoUrl("carlos.jpg");
        j1.setHaveTeam(true);
        j1.setAge(22);
        j1.setGender("Masculino");
        j1.setCaptain(true);
        j1.setSemester(6);
        playerRepository.save(j1);

        StudentPlayer j2 = new StudentPlayer();
        j2.setId("J002");
        j2.setFullname("Juan Pérez");
        j2.setEmail("juan.perez@escuelaing.edu.co");
        j2.setNumberID(123457);
        j2.setPosition(PositionEnum.Winger);
        j2.setDorsalNumber(9);
        j2.setPhotoUrl("juan.jpg");
        j2.setHaveTeam(true);
        j2.setAge(21);
        j2.setGender("Masculino");
        j2.setCaptain(false);
        j2.setSemester(5);
        playerRepository.save(j2);

        InstitutionalPlayer j3 = new InstitutionalPlayer();
        j3.setId("J003");
        j3.setFullname("Pedro Sánchez");
        j3.setEmail("pedro.sanchez@gmail.com");
        j3.setNumberID(123458);
        j3.setPosition(PositionEnum.Midfielder);
        j3.setDorsalNumber(8);
        j3.setPhotoUrl("pedro.jpg");
        j3.setHaveTeam(true);
        j3.setAge(23);
        j3.setGender("Masculino");
        j3.setCaptain(false);
        playerRepository.save(j3);

        StudentPlayer j4 = new StudentPlayer();
        j4.setId("J004");
        j4.setFullname("Luis Martínez");
        j4.setEmail("luis.martinez@escuelaing.edu.co");
        j4.setNumberID(123459);
        j4.setPosition(PositionEnum.GoalKeeper);
        j4.setDorsalNumber(1);
        j4.setPhotoUrl("luis.jpg");
        j4.setHaveTeam(true);
        j4.setAge(24);
        j4.setGender("Masculino");
        j4.setCaptain(true);
        j4.setSemester(8);
        playerRepository.save(j4);

        StudentPlayer j5 = new StudentPlayer();
        j5.setId("J005");
        j5.setFullname("Ana García");
        j5.setEmail("ana.garcia@escuelaing.edu.co");
        j5.setNumberID(123460);
        j5.setPosition(PositionEnum.Defender);
        j5.setDorsalNumber(3);
        j5.setPhotoUrl("ana.jpg");
        j5.setHaveTeam(true);
        j5.setAge(22);
        j5.setGender("Femenino");
        j5.setCaptain(false);
        j5.setSemester(6);
        playerRepository.save(j5);

        StudentPlayer j6 = new StudentPlayer();
        j6.setId("J006");
        j6.setFullname("Miguel Ángel López");
        j6.setEmail("miguel.lopez@escuelaing.edu.co");
        j6.setNumberID(123461);
        j6.setPosition(PositionEnum.Winger);
        j6.setDorsalNumber(11);
        j6.setPhotoUrl("miguel.jpg");
        j6.setHaveTeam(true);
        j6.setAge(20);
        j6.setGender("Masculino");
        j6.setCaptain(false);
        j6.setSemester(4);
        playerRepository.save(j6);

        StudentPlayer j7 = new StudentPlayer();
        j7.setId("J007");
        j7.setFullname("Diego Fernández");
        j7.setEmail("diego.fernandez@escuelaing.edu.co");
        j7.setNumberID(123462);
        j7.setPosition(PositionEnum.Midfielder);
        j7.setDorsalNumber(6);
        j7.setPhotoUrl("diego.jpg");
        j7.setHaveTeam(true);
        j7.setAge(23);
        j7.setGender("Masculino");
        j7.setCaptain(false);
        j7.setSemester(7);
        playerRepository.save(j7);

        StudentPlayer j8 = new StudentPlayer();
        j8.setId("J008");
        j8.setFullname("Sofía Ramírez");
        j8.setEmail("sofia.ramirez@escuelaing.edu.co");
        j8.setNumberID(123463);
        j8.setPosition(PositionEnum.GoalKeeper);
        j8.setDorsalNumber(12);
        j8.setPhotoUrl("sofia.jpg");
        j8.setHaveTeam(true);
        j8.setAge(21);
        j8.setGender("Femenino");
        j8.setCaptain(false);
        j8.setSemester(3);
        playerRepository.save(j8);

        StudentPlayer organizador = new StudentPlayer();
        organizador.setId("J-ORG");
        organizador.setFullname("Organizador TechCup");
        organizador.setEmail("organizador@escuelaing.edu.co");
        organizador.setNumberID(999999);
        organizador.setPosition(PositionEnum.GoalKeeper);
        organizador.setDorsalNumber(99);
        organizador.setAge(30);
        organizador.setGender("Masculino");
        organizador.setCaptain(true);
        organizador.setSemester(10);
        organizador.setPasswordHash(
                "$2a$10$tao5RmTFzFWXFF1wze8hJ.W6C3xMkbmr3aWr7xxQAsQW1gwivDi5C");
        organizador.setSystemRole(SystemRole.ORGANIZADOR);
        playerRepository.save(organizador);
    }

    private void inicializarEquipos() {
        Player j1 = playerRepository.findById("J001").orElseThrow();
        Player j2 = playerRepository.findById("J002").orElseThrow();
        Player j3 = playerRepository.findById("J003").orElseThrow();
        Player j4 = playerRepository.findById("J004").orElseThrow();
        Player j5 = playerRepository.findById("J005").orElseThrow();
        Player j6 = playerRepository.findById("J006").orElseThrow();
        Player j7 = playerRepository.findById("J007").orElseThrow();
        Player j8 = playerRepository.findById("J008").orElseThrow();

        Team e1 = new Team();
        e1.setId("E001");
        e1.setTeamName("Los Galácticos");
        e1.setShieldUrl("galacticos_escudo.png");
        e1.setUniformColors(List.of("#FFFFFF", "#FFD700", "#C0C0C0"));
        e1.setCaptain(j1);
        List<Player> pe1 = new ArrayList<>();
        pe1.add(j1);
        pe1.add(j2);
        pe1.add(j3);
        pe1.add(j5);
        e1.setPlayers(pe1);
        teamRepository.save(e1);

        Team e2 = new Team();
        e2.setId("E002");
        e2.setTeamName("Los Titanes");
        e2.setShieldUrl("titanes_escudo.png");
        e2.setUniformColors(List.of("#0057A8", "#FFFFFF", "#003F7F"));
        e2.setCaptain(j4);
        List<Player> pe2 = new ArrayList<>();
        pe2.add(j4);
        pe2.add(j7);
        e2.setPlayers(pe2);
        teamRepository.save(e2);

        Team e3 = new Team();
        e3.setId("E003");
        e3.setTeamName("Los Guerreros");
        e3.setShieldUrl("guerreros_escudo.png");
        e3.setUniformColors(List.of("#228B22", "#000000", "#32CD32"));
        e3.setCaptain(j6);
        List<Player> pe3 = new ArrayList<>();
        pe3.add(j6);
        pe3.add(j8);
        e3.setPlayers(pe3);
        teamRepository.save(e3);
    }

    private void inicializarTorneos() {
        Tournament t1 = new Tournament();
        t1.setId("T001");
        t1.setName("Torneo de Verano 2024");
        t1.setStartDate(LocalDateTime.of(2024, 6, 1, 9, 0));
        t1.setEndDate(LocalDateTime.of(2024, 8, 31, 18, 0));
        t1.setRegistrationFee(150.0);
        t1.setMaxTeams(8);
        t1.setRules("Reglas estándar de fútbol 11");
        t1.setCurrentState(TournamentState.ACTIVE);
        tournamentRepository.save(t1);

        Tournament t2 = new Tournament();
        t2.setId("T002");
        t2.setName("Torneo de Invierno 2024");
        t2.setStartDate(LocalDateTime.of(2024, 12, 1, 9, 0));
        t2.setEndDate(LocalDateTime.of(2025, 1, 31, 18, 0));
        t2.setRegistrationFee(200.0);
        t2.setMaxTeams(10);
        t2.setRules("Reglas estándar de fútbol 11");
        t2.setCurrentState(TournamentState.DELETED);
        tournamentRepository.save(t2);

        Tournament t3 = new Tournament();
        t3.setId("T003");
        t3.setName("Torneo Relámpago");
        t3.setStartDate(LocalDateTime.of(2024, 3, 1, 10, 0));
        t3.setEndDate(LocalDateTime.of(2024, 3, 30, 18, 0));
        t3.setRegistrationFee(100.0);
        t3.setMaxTeams(6);
        t3.setRules("Formato de eliminación directa");
        t3.setCurrentState(TournamentState.IN_PROGRESS);
        tournamentRepository.save(t3);
    }
}
