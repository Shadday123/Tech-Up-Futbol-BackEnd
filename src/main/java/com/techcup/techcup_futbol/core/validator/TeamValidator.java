package com.techcup.techcup_futbol.core.validator;


import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.exception.TeamException;
;
import java.util.*;

public class TeamValidator {

    private static final int MIN_PLAYERS = 7;
    private static final int MAX_PLAYERS = 12;

    private TeamValidator() {}

    public static void validate(Team equipo, List<Team> todosLosEquipos) throws TeamException {

        validateTeam(equipo);
        validatePlayerCount(equipo);
        validateDuplicatePlayers(equipo, todosLosEquipos);
        validateStudentPlayers(equipo);
    }

    private static void validateTeam(Team equipo) throws TeamException {

        if (equipo == null) {
            throw new TeamException("El equipo es null");
        }

        if (equipo.getPlayers() == null) {
            throw new TeamException("El equipo " + equipo.getTeamName() + " tiene lista de jugadores null");
        }

        if (equipo.getPlayers().isEmpty()) {
            throw new TeamException("El equipo " + equipo.getTeamName() + " no tiene jugadores");
        }
    }

    private static void validatePlayerCount(Team equipo) throws TeamException {

        int size = equipo.getPlayers().size();

        if (size < MIN_PLAYERS) {
            throw new TeamException(String.format(
                    "Equipo %s tiene %d jugadores. Mínimo %d",
                    equipo.getTeamName(), size, MIN_PLAYERS));
        }

        if (size > MAX_PLAYERS) {
            throw new TeamException(String.format(
                    "Equipo %s tiene %d jugadores. Máximo %d",
                    equipo.getTeamName(), size, MAX_PLAYERS));
        }
    }

    private static void validateDuplicatePlayers(Team equipo, List<Team> todosLosEquipos)
            throws TeamException {

        if (todosLosEquipos == null) return;

        Map<String, List<String>> playerTeams = new HashMap<>();

        for (Team t : todosLosEquipos) {

            if (t == null || t.getPlayers() == null) continue;

            for (Player p : t.getPlayers()) {

                if (p == null || p.getId() == null) continue;

                playerTeams
                        .computeIfAbsent(p.getId(), k -> new ArrayList<>())
                        .add(t.getTeamName());
            }
        }

        for (Player jugador : equipo.getPlayers()) {

            if (jugador == null || jugador.getId() == null) continue;

            List<String> teams = playerTeams.get(jugador.getId());

            if (teams != null && teams.size() > 1) {
                throw new TeamException(String.format(
                        "Jugador %s está inscrito en %d equipos: %s",
                        jugador.getFullname(),
                        teams.size(),
                        String.join(", ", teams)
                ));
            }
        }
    }

    private static void validateStudentPlayers(Team equipo) throws TeamException {

        List<Player> jugadores = equipo.getPlayers();

        long estudiantes = jugadores.stream()
                .filter(j -> j instanceof StudentPlayer)
                .count();

        int totalJugadores = jugadores.size();
        int minimoEstudiantes = totalJugadores / 2;

        if (estudiantes < minimoEstudiantes) {
            throw new TeamException(String.format(
                    "El equipo %s tiene %d estudiantes de %d jugadores. Debe tener al menos %d estudiantes",
                    equipo.getTeamName(),
                    estudiantes,
                    totalJugadores,
                    minimoEstudiantes
            ));
        }
    }
}