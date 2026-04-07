package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.model.Referee;
import java.util.ArrayList;
import com.techcup.techcup_futbol.core.exception.RefereeException;
import com.techcup.techcup_futbol.persistence.repository.MatchRepository;
import com.techcup.techcup_futbol.persistence.repository.RefereeRepository;
import com.techcup.techcup_futbol.core.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RefereeServiceImpl implements RefereeService {

    private static final Logger log = LoggerFactory.getLogger(RefereeServiceImpl.class);

    @Autowired
    private RefereeRepository refereeRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Override
    @Transactional
    public Referee create(String fullname, String email) {
        log.info("Registrando árbitro: {}", fullname);

        if (refereeRepository.existsByEmail(email)) {
            throw new RefereeException("email",
                    String.format(RefereeException.EMAIL_ALREADY_REGISTERED, email));
        }

        Referee referee = new Referee();
        referee.setId(IdGenerator.generateId());
        referee.setFullname(fullname);
        referee.setEmail(email);

        refereeRepository.save(referee);
        log.info("Árbitro registrado ID: {}", referee.getId());
        return referee;
    }

    @Override
    @Transactional
    public Referee assignToMatch(String matchId, String refereeId) {
        log.info("Asignando árbitro ID: {} al partido ID: {}", refereeId, matchId);

        Referee referee = refereeRepository.findById(refereeId)
                .orElseThrow(() -> new RefereeException("refereeId",
                        String.format(RefereeException.REFEREE_NOT_FOUND, refereeId)));

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RefereeException("matchId",
                        String.format(RefereeException.REFEREE_NOT_FOUND, matchId)));

        if (match.getReferee() != null) {
            throw new RefereeException("match", RefereeException.MATCH_ALREADY_HAS_REFEREE);
        }

        match.setReferee(referee);
        matchRepository.save(match);

        if (referee.getAssignedMatches() == null) {
            referee.setAssignedMatches(new ArrayList<>());
        }
        referee.getAssignedMatches().add(match);

        log.info("Árbitro '{}' asignado al partido {}", referee.getFullname(), matchId);
        return referee;
    }

    @Override
    public Referee findById(String refereeId) {
        return refereeRepository.findById(refereeId)
                .orElseThrow(() -> new RefereeException("id",
                        String.format(RefereeException.REFEREE_NOT_FOUND, refereeId)));
    }

    @Override
    public List<Referee> findAll() {
        return refereeRepository.findAll();
    }
}
