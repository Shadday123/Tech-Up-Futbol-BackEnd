package com.techcup.techcup_futbol.model;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DataStore {

    public static Map<String, Player> jugadores = new HashMap<>();
    public static Map<String, Tournament> torneos = new HashMap<>();

    public static void inicializarDatos() {
        inicializarJugadores();
        inicializarTorneos();
    }

    private static void inicializarJugadores() {
        Player jugador1 = new Player();
        jugador1.setNombre("Carlos Rodríguez");
        jugador1.setCorreo("carlos.rodriguez@gmail.com");
        jugador1.setTipo(TipoUsuario.CAPITAN.toString());
        jugador1.setPosiciones(Arrays.asList(Posicion.DEFENSA, Posicion.LATERAL_DERECHO));
        jugador1.setDorsal("4");
        jugador1.setFoto("carlos.jpg");
        jugador1.setDisponible(true);
        jugadores.put("J001", jugador1);

        Player jugador2 = new Player();
        jugador2.setNombre("Juan Pérez");
        jugador2.setCorreo("juan.perez@escuelaing.edu.co");
        jugador2.setTipo(TipoUsuario.JUGADOR.toString());
        jugador2.setPosiciones(Arrays.asList(Posicion.DELANTERO, Posicion.EXTREMO_DERECHO));
        jugador2.setDorsal("9");
        jugador2.setFoto("juan.jpg");
        jugador2.setDisponible(true);
        jugadores.put("J002", jugador2);

        Player jugador3 = new Player();
        jugador3.setNombre("Pedro Sánchez");
        jugador3.setCorreo("pedro.sanchez@gmail.com");
        jugador3.setTipo(TipoUsuario.JUGADOR.toString());
        jugador3.setPosiciones(Arrays.asList(Posicion.MEDIOCAMPISTA));
        jugador3.setDorsal("8");
        jugador3.setFoto("pedro.jpg");
        jugador3.setDisponible(true);
        jugadores.put("J003", jugador3);

        Player jugador4 = new Player();
        jugador4.setNombre("Luis Martínez");
        jugador4.setCorreo("luis.martinez@escuelaing.edu.co");
        jugador4.setTipo(TipoUsuario.CAPITAN.toString());
        jugador4.setPosiciones(Arrays.asList(Posicion.PORTERO));
        jugador4.setDorsal("1");
        jugador4.setFoto("luis.jpg");
        jugador4.setDisponible(true);
        jugadores.put("J004", jugador4);


        Player jugador5 = new Player();
        jugador5.setNombre("Ana García");
        jugador5.setCorreo("ana.garcia@gmail.com");
        jugador5.setTipo(TipoUsuario.JUGADOR.toString());
        jugador5.setPosiciones(Arrays.asList(Posicion.LATERAL_IZQUIERDO, Posicion.DEFENSA));
        jugador5.setDorsal("3");
        jugador5.setFoto("ana.jpg");
        jugador5.setDisponible(true);
        jugadores.put("J005", jugador5);

        // Jugador 6 (correo @escuelaing.edu.co)
        Player jugador6 = new Player();
        jugador6.setNombre("Miguel Ángel López");
        jugador6.setCorreo("miguel.lopez@escuelaing.edu.co");
        jugador6.setTipo(TipoUsuario.JUGADOR.toString());
        jugador6.setPosiciones(Arrays.asList(Posicion.EXTREMO_IZQUIERDO, Posicion.DELANTERO));
        jugador6.setDorsal("11");
        jugador6.setFoto("miguel.jpg");
        jugador6.setDisponible(true);
        jugadores.put("J006", jugador6);

        Player jugador7 = new Player();
        jugador7.setNombre("Laura Torres");
        jugador7.setCorreo("laura.torres@gmail.com");
        jugador7.setTipo(TipoUsuario.JUGADOR.toString());
        jugador7.setPosiciones(Arrays.asList(Posicion.MEDIOCAMPISTA, Posicion.DEFENSA));
        jugador7.setDorsal("6");
        jugador7.setFoto("laura.jpg");
        jugador7.setDisponible(true);
        jugadores.put("J007", jugador7);
    }


    private static void inicializarTorneos() {
        Tournament torneo1 = new Tournament();
        Calendar cal = Calendar.getInstance();

        cal.set(2024, Calendar.JUNE, 1);
        torneo1.setFechainicial(cal.getTime());

        cal.set(2024, Calendar.AUGUST, 31);
        torneo1.setFechaFinal(cal.getTime());

        torneo1.setCantEquipos(8);
        torneo1.setCostoPorEquipo(150.0);
        torneo1.setEstado(StatusTournament.ACTIVO);
        torneos.put("T001", torneo1);

        Tournament torneo2 = new Tournament();
        cal.set(2024, Calendar.DECEMBER, 1);
        torneo2.setFechainicial(cal.getTime());

        cal.set(2025, Calendar.JANUARY, 31);
        torneo2.setFechaFinal(cal.getTime());

        torneo2.setCantEquipos(10);
        torneo2.setCostoPorEquipo(200.0);
        torneo2.setEstado(StatusTournament.BORRADOR);
        torneos.put("T002", torneo2);

        Tournament torneo3 = new Tournament();
        cal.set(2024, Calendar.MARCH, 1);
        torneo3.setFechainicial(cal.getTime());

        cal.set(2024, Calendar.MARCH, 30);
        torneo3.setFechaFinal(cal.getTime());

        torneo3.setCantEquipos(6);
        torneo3.setCostoPorEquipo(100.0);
        torneo3.setEstado(StatusTournament.EN_PROGRESO);
        torneos.put("T003", torneo3);
    }}
