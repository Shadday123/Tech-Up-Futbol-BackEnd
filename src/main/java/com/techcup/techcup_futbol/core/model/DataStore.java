package com.techcup.techcup_futbol.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class DataStore {

    public static Map<String, Player> jugadores = new HashMap<>();
    public static Map<String, Team> equipos = new HashMap<>();
    public static Map<String, Tournament> torneos = new HashMap<>();

    public static void inicializarDatos() {
        inicializarJugadores();
        inicializarEquipos();
        inicializarTorneos();
    }

    // JUGADORES
    private static void inicializarJugadores() {

        // StudentPlayer - Capitán equipo 1
        StudentPlayer jugador1 = new StudentPlayer();
        jugador1.setId("J001");
        jugador1.setFullname("Carlos Rodríguez");
        jugador1.setEmail("carlos.rodriguez@escuelaing.edu.co");
        jugador1.setNumberID(123456);
        jugador1.setPosition(PositionEnum.Defender);
        jugador1.setDorsalNumber(4);
        jugador1.setPhotoUrl("carlos.jpg");
        jugador1.setHaveTeam(true);
        jugador1.setAge(22);
        jugador1.setGender("Masculino");
        jugador1.setCaptain(true);
        jugador1.setSemester(6);
        jugadores.put(jugador1.getId(), jugador1);

        // StudentPlayer - equipo 1
        StudentPlayer jugador2 = new StudentPlayer();
        jugador2.setId("J002");
        jugador2.setFullname("Juan Pérez");
        jugador2.setEmail("juan.perez@escuelaing.edu.co");
        jugador2.setNumberID(123457);
        jugador2.setPosition(PositionEnum.Winger);
        jugador2.setDorsalNumber(9);
        jugador2.setPhotoUrl("juan.jpg");
        jugador2.setHaveTeam(true);
        jugador2.setAge(21);
        jugador2.setGender("Masculino");
        jugador2.setCaptain(false);
        jugador2.setSemester(5);
        jugadores.put(jugador2.getId(), jugador2);

        // InstitutionalPlayer - equipo 1
        InstitutionalPlayer jugador3 = new InstitutionalPlayer();
        jugador3.setId("J003");
        jugador3.setFullname("Pedro Sánchez");
        jugador3.setEmail("pedro.sanchez@gmail.com");
        jugador3.setNumberID(123458);
        jugador3.setPosition(PositionEnum.Midfielder);
        jugador3.setDorsalNumber(8);
        jugador3.setPhotoUrl("pedro.jpg");
        jugador3.setHaveTeam(true);
        jugador3.setAge(23);
        jugador3.setGender("Masculino");
        jugador3.setCaptain(false);
        jugadores.put(jugador3.getId(), jugador3);

        // StudentPlayer - Capitán equipo 2
        StudentPlayer jugador4 = new StudentPlayer();
        jugador4.setId("J004");
        jugador4.setFullname("Luis Martínez");
        jugador4.setEmail("luis.martinez@escuelaing.edu.co");
        jugador4.setNumberID(123459);
        jugador4.setPosition(PositionEnum.GoalKeeper);
        jugador4.setDorsalNumber(1);
        jugador4.setPhotoUrl("luis.jpg");
        jugador4.setHaveTeam(true);
        jugador4.setAge(24);
        jugador4.setGender("Masculino");
        jugador4.setCaptain(true);
        jugador4.setSemester(8);
        jugadores.put(jugador4.getId(), jugador4);

        // StudentPlayer - equipo 2
        StudentPlayer jugador5 = new StudentPlayer();
        jugador5.setId("J005");
        jugador5.setFullname("Ana García");
        jugador5.setEmail("ana.garcia@escuelaing.edu.co");
        jugador5.setNumberID(123460);
        jugador5.setPosition(PositionEnum.Defender);
        jugador5.setDorsalNumber(3);
        jugador5.setPhotoUrl("ana.jpg");
        jugador5.setHaveTeam(true);
        jugador5.setAge(22);
        jugador5.setGender("Femenino");
        jugador5.setCaptain(false);
        jugador5.setSemester(6);
        jugadores.put(jugador5.getId(), jugador5);

        // StudentPlayer - equipo 3
        StudentPlayer jugador6 = new StudentPlayer();
        jugador6.setId("J006");
        jugador6.setFullname("Miguel Ángel López");
        jugador6.setEmail("miguel.lopez@escuelaing.edu.co");
        jugador6.setNumberID(123461);
        jugador6.setPosition(PositionEnum.Winger);
        jugador6.setDorsalNumber(11);
        jugador6.setPhotoUrl("miguel.jpg");
        jugador6.setHaveTeam(true);
        jugador6.setAge(20);
        jugador6.setGender("Masculino");
        jugador6.setCaptain(false);
        jugador6.setSemester(4);
        jugadores.put(jugador6.getId(), jugador6);


        // StudentPlayer - equipo 3 (reemplaza a J002/J005 que estaban duplicados)
        StudentPlayer jugador8 = new StudentPlayer();
        jugador8.setId("J008");
        jugador8.setFullname("Sofía Ramírez");
        jugador8.setEmail("sofia.ramirez@escuelaing.edu.co");
        jugador8.setNumberID(123463);
        jugador8.setPosition(PositionEnum.GoalKeeper);
        jugador8.setDorsalNumber(12);
        jugador8.setPhotoUrl("sofia.jpg");
        jugador8.setHaveTeam(true);
        jugador8.setAge(21);
        jugador8.setGender("Femenino");
        jugador8.setCaptain(false);
        jugador8.setSemester(3);
        jugadores.put(jugador8.getId(), jugador8);
    }

    // EQUIPOS
    private static void inicializarEquipos() {

        // Equipo 1 - Los Galácticos: J001, J002, J003, J005
        Team equipo1 = new Team();
        equipo1.setId("E001");
        equipo1.setTeamName("Los Galácticos");
        equipo1.setShieldUrl("galacticos_escudo.png");
        equipo1.setUniformColors("Blanco y Dorado");
        equipo1.setCaptain(jugadores.get("J001"));

        List<Player> jugadoresEquipo1 = new ArrayList<>();
        jugadoresEquipo1.add(jugadores.get("J001"));
        jugadoresEquipo1.add(jugadores.get("J002"));
        jugadoresEquipo1.add(jugadores.get("J003"));
        jugadoresEquipo1.add(jugadores.get("J005"));
        equipo1.setPlayers(jugadoresEquipo1);

        equipos.put(equipo1.getId(), equipo1);

        // Equipo 2 - Los Titanes: J004, J007 (ya no incluye J002 ni J005)
        Team equipo2 = new Team();
        equipo2.setId("E002");
        equipo2.setTeamName("Los Titanes");
        equipo2.setShieldUrl("titanes_escudo.png");
        equipo2.setUniformColors("Azul y Blanco");
        equipo2.setCaptain(jugadores.get("J004"));

        List<Player> jugadoresEquipo2 = new ArrayList<>();
        jugadoresEquipo2.add(jugadores.get("J004"));
        jugadoresEquipo2.add(jugadores.get("J007"));
        equipo2.setPlayers(jugadoresEquipo2);

        equipos.put(equipo2.getId(), equipo2);

        // Equipo 3 - Los Guerreros: J003 era capitán pero ya está en E001,
        // se asigna J006 como capitán y se agregan J006, J008
        Team equipo3 = new Team();
        equipo3.setId("E003");
        equipo3.setTeamName("Los Guerreros");
        equipo3.setShieldUrl("guerreros_escudo.png");
        equipo3.setUniformColors("Verde y Negro");
        equipo3.setCaptain(jugadores.get("J006"));

        List<Player> jugadoresEquipo3 = new ArrayList<>();
        jugadoresEquipo3.add(jugadores.get("J006"));
        jugadoresEquipo3.add(jugadores.get("J008"));
        equipo3.setPlayers(jugadoresEquipo3);

        equipos.put(equipo3.getId(), equipo3);
    }

    // TORNEOS
    private static void inicializarTorneos() {
        Tournament torneo1 = new Tournament();
        torneo1.setId("T001");
        torneo1.setName("Torneo de Verano 2024");
        torneo1.setStartDate(LocalDateTime.of(2024, 6, 1, 9, 0));
        torneo1.setEndDate(LocalDateTime.of(2024, 8, 31, 18, 0));
        torneo1.setRegistrationFee(150.0);
        torneo1.setMaxTeams(8);
        torneo1.setRules("Reglas estándar de fútbol 11");
        torneo1.setCurrentState(TournamentState.ACTIVE);
        torneos.put(torneo1.getId(), torneo1);

        Tournament torneo2 = new Tournament();
        torneo2.setId("T002");
        torneo2.setName("Torneo de Invierno 2024");
        torneo2.setStartDate(LocalDateTime.of(2024, 12, 1, 9, 0));
        torneo2.setEndDate(LocalDateTime.of(2025, 1, 31, 18, 0));
        torneo2.setRegistrationFee(200.0);
        torneo2.setMaxTeams(10);
        torneo2.setRules("Reglas estándar de fútbol 11");
        torneo2.setCurrentState(TournamentState.DELETED);
        torneos.put(torneo2.getId(), torneo2);

        Tournament torneo3 = new Tournament();
        torneo3.setId("T003");
        torneo3.setName("Torneo Relámpago");
        torneo3.setStartDate(LocalDateTime.of(2024, 3, 1, 10, 0));
        torneo3.setEndDate(LocalDateTime.of(2024, 3, 30, 18, 0));
        torneo3.setRegistrationFee(100.0);
        torneo3.setMaxTeams(6);
        torneo3.setRules("Formato de eliminación directa");
        torneo3.setCurrentState(TournamentState.IN_PROGRESS);
        torneos.put(torneo3.getId(), torneo3);
    }

    public static void limpiarDatos() {
        jugadores.clear();
        equipos.clear();
        torneos.clear();
    }
}