package com.techcup.techcup_futbol.validator;

import com.techcup.techcup_futbol.model.*;
import java.util.*;
import java.util.stream.Collectors;

class TeamException extends Exception {
    public TeamException(String message) {
        super(message);
    }
}

public class TeamValidator {

    private static final int MIN_PLAYERS = 7;
    private static final int MAX_PLAYERS = 12;
    private static final double MIN_STUDENT_PERCENTAGE = 50.0;

    private TeamValidator() {}

    public static void validate(Team equipo, List<Team> todosLosEquipos)
            throws TeamException {

        List<String> errores = new ArrayList<>();

        validateTeam(equipo, errores);
        validatePlayerCount(equipo, errores);
        validateDuplicatePlayers(equipo, todosLosEquipos, errores);
        validateStudentPercentage(equipo, errores);

        if (!errores.isEmpty()) {
            throw new TeamException(String.join("; ", errores));
        }
    }

    private static void validateTeam(Team equipo, List<String> errores) {
        if (equipo == null) {
            errores.add("El equipo es null");
            return;
        }

        if (equipo.getPlayers() == null) {
            errores.add("El equipo " + equipo.getTeamName() + " tiene lista de jugadores null");
        } else if (equipo.getPlayers().isEmpty()) {
            errores.add("El equipo " + equipo.getTeamName() + " no tiene jugadores");
        }
    }

    private static void validatePlayerCount(Team equipo, List<String> errores) {
        if (equipo == null || equipo.getPlayers() == null) return;

        int size = equipo.getPlayers().size();

        if (size < MIN_PLAYERS) {
            errores.add(String.format("Equipo %s tiene %d jugadores. Mínimo %d",
                    equipo.getTeamName(), size, MIN_PLAYERS));
        }

        if (size > MAX_PLAYERS) {
            errores.add(String.format("Equipo %s tiene %d jugadores. Máximo %d",
                    equipo.getTeamName(), size, MAX_PLAYERS));
        }
    }

    private static void validateDuplicatePlayers(Team equipo, List<Team> todosLosEquipos,
                                                 List<String> errores) {
        if (equipo == null || equipo.getPlayers() == null || todosLosEquipos == null) return;

        Map<String, List<String>> playerTeams = new HashMap<>();

        for (Team t : todosLosEquipos) {
            if (t == null || t.getPlayers() == null) continue;

            for (Player p : t.getPlayers()) {
                if (p == null || p.getId() == null) continue;
                playerTeams.computeIfAbsent(p.getId(), k -> new ArrayList<>()).add(t.getTeamName());
            }
        }

        for (Player jugador : equipo.getPlayers()) {
            if (jugador == null || jugador.getId() == null) continue;

            List<String> teams = playerTeams.get(jugador.getId());
            if (teams != null && teams.size() > 1) {
                errores.add(String.format("Jugador %s está inscrito en %d equipos: %s",
                        jugador.getFullname(), teams.size(), String.join(", ", teams)));
            }
        }
    }

    private static void validateStudentPercentage(Team equipo, List<String> errores) {
        if (equipo == null || equipo.getPlayers() == null) return;

        List<Player> jugadores = equipo.getPlayers();

        long estudiantes = jugadores.stream()
                .filter(p -> p instanceof StudentPlayer)
                .count();

        double porcentaje = (estudiantes * 100.0) / jugadores.size();

        if (porcentaje <= MIN_STUDENT_PERCENTAGE) {
            String tiposJugadores = jugadores.stream()
                    .map(p -> p.getClass().getSimpleName())
                    .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                    .entrySet().stream()
                    .map(e -> e.getValue() + " " + e.getKey())
                    .collect(Collectors.joining(", "));

            errores.add(String.format(
                    "Equipo %s tiene solo %d/%d estudiantes (%.1f%%). Debe ser >%.0f%%. Composición: %s",
                    equipo.getTeamName(), estudiantes, jugadores.size(),
                    porcentaje, MIN_STUDENT_PERCENTAGE, tiposJugadores));
        }
    }
}