package com.techcup.techcup_futbol.core.validator;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.exception.TeamException;

import java.util.*;

public class TeamValidator {

    private static final int MIN_PLAYERS = 7;
    private static final int MAX_PLAYERS = 12;

    private TeamValidator() {}

    /** Validación completa antes de inscribir un equipo en un torneo. */
    public static void validate(Team equipo, List<Team> todosLosEquipos) {
        validateNotNull(equipo);
        validatePlayerCount(equipo);
        validateDuplicatePlayers(equipo, todosLosEquipos);
        validateStudentPlayers(equipo);
    }

    public static void validateTeamName(String name, List<Team> todosLosEquipos) {
        if (name == null || name.isBlank()) {
            throw new TeamException("teamName", TeamException.TEAM_NAME_EMPTY);
        }
        if (todosLosEquipos != null) {
            boolean exists = todosLosEquipos.stream()
                    .anyMatch(t -> t.getTeamName().equalsIgnoreCase(name));
            if (exists) {
                throw new TeamException("teamName",
                        String.format(TeamException.TEAM_NAME_ALREADY_EXISTS, name));
            }
        }
    }

    public static void validateCaptain(Team equipo) {
        if (equipo.getCaptain() == null) {
            throw new TeamException("captain",
                    String.format(TeamException.CAPTAIN_REQUIRED, equipo.getTeamName()));
        }
    }

    public static void validatePlayerAddition(Team equipo, Player jugador) {
        if (jugador == null) {
            throw new TeamException("player", TeamException.PLAYER_NULL);
        }
        if (jugador.isHaveTeam()) {
            throw new TeamException("player",
                    String.format(TeamException.PLAYER_ALREADY_HAS_TEAM, jugador.getFullname()));
        }
        int currentSize = equipo.getPlayers() != null ? equipo.getPlayers().size() : 0;
        if (currentSize >= MAX_PLAYERS) {
            throw new TeamException("players",
                    String.format(TeamException.TEAM_FULL, equipo.getTeamName(), MAX_PLAYERS));
        }
    }

    private static void validateNotNull(Team equipo) {
        if (equipo == null) {
            throw new TeamException(TeamException.TEAM_NULL);
        }
        if (equipo.getPlayers() == null) {
            throw new TeamException("players",
                    String.format(TeamException.PLAYERS_LIST_NULL, equipo.getTeamName()));
        }
        if (equipo.getPlayers().isEmpty()) {
            throw new TeamException("players",
                    String.format(TeamException.PLAYERS_LIST_EMPTY, equipo.getTeamName()));
        }
    }

    private static void validatePlayerCount(Team equipo) {
        int size = equipo.getPlayers().size();
        if (size < MIN_PLAYERS) {
            throw new TeamException("players",
                    String.format(TeamException.PLAYERS_BELOW_MINIMUM,
                            equipo.getTeamName(), size, MIN_PLAYERS));
        }
        if (size > MAX_PLAYERS) {
            throw new TeamException("players",
                    String.format(TeamException.PLAYERS_ABOVE_MAXIMUM,
                            equipo.getTeamName(), size, MAX_PLAYERS));
        }
    }

    private static void validateDuplicatePlayers(Team equipo, List<Team> todosLosEquipos) {
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
            List<String> equiposDelJugador = playerTeams.get(jugador.getId());
            if (equiposDelJugador != null && equiposDelJugador.size() > 1) {
                throw new TeamException("players",
                        String.format(TeamException.PLAYER_IN_MULTIPLE_TEAMS,
                                jugador.getFullname(),
                                equiposDelJugador.size(),
                                String.join(", ", equiposDelJugador)));
            }
        }
    }

    private static void validateStudentPlayers(Team equipo) {
        List<Player> jugadores = equipo.getPlayers();

        long estudiantes = jugadores.stream()
                .filter(j -> j instanceof StudentPlayer)
                .count();

        int total = jugadores.size();
        int minEstudiantes = total / 2;

        if (estudiantes < minEstudiantes) {
            throw new TeamException("players",
                    String.format(TeamException.NOT_ENOUGH_STUDENTS,
                            equipo.getTeamName(), estudiantes, total, minEstudiantes));
        }
    }
}