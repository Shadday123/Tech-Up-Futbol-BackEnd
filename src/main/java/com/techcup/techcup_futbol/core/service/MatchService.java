package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.model.MatchEventInput;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MatchService {

    Match create(String localTeamId, String visitorTeamId, LocalDateTime dateTime,
                 String refereeId, int field);

    Match registerResult(String matchId, int scoreLocal, int scoreVisitor,
                         List<MatchEventInput> events);

    Match findById(String matchId);

    List<Match> findAll();

    List<Match> findByTeamId(String teamId);

    boolean isResultRegistered(String matchId);

    void registerMatch(Match match);

    Map<String, Match> getMatches();
}
