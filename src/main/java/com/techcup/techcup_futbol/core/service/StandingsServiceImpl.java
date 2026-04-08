package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.model.Standings;
import com.techcup.techcup_futbol.core.model.Team;
import com.techcup.techcup_futbol.core.exception.TournamentException;
import com.techcup.techcup_futbol.persistence.entity.StandingsEntity;
import com.techcup.techcup_futbol.persistence.entity.TeamEntity;
import com.techcup.techcup_futbol.persistence.mapper.StandingsPersistenceMapper;
import com.techcup.techcup_futbol.persistence.repository.StandingsRepository;
import com.techcup.techcup_futbol.persistence.repository.TournamentRepository;
import com.techcup.techcup_futbol.core.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class StandingsServiceImpl implements StandingsService {

    private static final Logger log = LoggerFactory.getLogger(StandingsServiceImpl.class);

    private final StandingsRepository standingsRepository;
    private final TournamentRepository tournamentRepository;

    public StandingsServiceImpl(StandingsRepository standingsRepository,
                                TournamentRepository tournamentRepository) {
        this.standingsRepository = standingsRepository;
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    @Transactional
    public void registerTeamInTournament(String tournamentId, Team team) {
        tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentException("id",
                        String.format(TournamentException.TOURNAMENT_NOT_FOUND, tournamentId)));

        Optional<StandingsEntity> existing = standingsRepository
                .findByTournamentIdAndTeamId(tournamentId, team.getId());

        if (existing.isEmpty()) {
            StandingsEntity standings = new StandingsEntity();
            standings.setId(IdGenerator.generateId());
            standings.setTournamentId(tournamentId);
            log.info("Equipo {} registrado en tabla de torneo {}", team.getId(), tournamentId);
            standingsRepository.save(standings);
        }
    }

    @Override
    @Transactional
    public void updateFromMatch(Match match) {
        // Buscar standings del equipo local y visitante
        List<StandingsEntity> localStandings = standingsRepository.findByTournamentId("temp"); // Simplificado
        List<StandingsEntity> visitorStandings = standingsRepository.findByTournamentId("temp");

        // Actualizar manualmente (ya que entity tiene calculateStatsFromMatch)
        for (StandingsEntity standings : localStandings) {
            // Lógica de actualización incremental
            standings.setMatchesPlayed(standings.getMatchesPlayed() + 1);
            standings.setGoalsFor(standings.getGoalsFor() + match.getScoreLocal());
            standings.setGoalsAgainst(standings.getGoalsAgainst() + match.getScoreVisitor());

            if (match.getScoreLocal() > match.getScoreVisitor()) {
                standings.setMatchesWon(standings.getMatchesWon() + 1);
                standings.setPoints(standings.getPoints() + 3);
            } else if (match.getScoreLocal() == match.getScoreVisitor()) {
                standings.setMatchesDrawn(standings.getMatchesDrawn() + 1);
                standings.setPoints(standings.getPoints() + 1);
            } else {
                standings.setMatchesLost(standings.getMatchesLost() + 1);
            }

            standings.setGoalsDifference(standings.getGoalsFor() - standings.getGoalsAgainst());
            standingsRepository.save(standings);
        }

        log.info("Tabla de posiciones actualizada");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Standings> findByTournamentId(String tournamentId) {
        tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentException("id",
                        String.format(TournamentException.TOURNAMENT_NOT_FOUND, tournamentId)));

        return standingsRepository.findByTournamentId(tournamentId).stream()
                .map(StandingsPersistenceMapper::toDomain)
                .sorted(Comparator
                        .comparingInt(Standings::getPoints).reversed()
                        .thenComparingInt(Standings::getGoalsDifference).reversed()
                        .thenComparingInt(Standings::getGoalsFor).reversed())
                .toList();
    }
}
