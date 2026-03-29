package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.AssignRefereeRequest;
import com.techcup.techcup_futbol.Controller.dto.CreateRefereeRequest;
import com.techcup.techcup_futbol.Controller.dto.RefereeResponse;
import com.techcup.techcup_futbol.Controller.mapper.RefereeMapper;
import com.techcup.techcup_futbol.core.model.Match;
import com.techcup.techcup_futbol.core.model.Referee;
import java.util.ArrayList;
import com.techcup.techcup_futbol.core.exception.RefereeException;
import com.techcup.techcup_futbol.repository.MatchRepository;
import com.techcup.techcup_futbol.repository.RefereeRepository;
import com.techcup.techcup_futbol.util.IdGenerator;
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
    public RefereeResponse create(CreateRefereeRequest request) {
        log.info("Registrando árbitro: {}", request.fullname());

        if (refereeRepository.existsByEmail(request.email())) {
            throw new RefereeException("email",
                    String.format(RefereeException.EMAIL_ALREADY_REGISTERED, request.email()));
        }

        Referee referee = new Referee();
        referee.setId(IdGenerator.generateId());
        referee.setFullname(request.fullname());
        referee.setEmail(request.email());

        refereeRepository.save(referee);
        log.info("Árbitro registrado ID: {}", referee.getId());
        return RefereeMapper.toResponse(referee);
    }

    @Override
    @Transactional
    public RefereeResponse assignToMatch(String matchId, AssignRefereeRequest request) {
        log.info("Asignando árbitro ID: {} al partido ID: {}", request.refereeId(), matchId);

        Referee referee = refereeRepository.findById(request.refereeId())
                .orElseThrow(() -> new RefereeException("refereeId",
                        String.format(RefereeException.REFEREE_NOT_FOUND, request.refereeId())));

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
        return RefereeMapper.toResponse(referee);
    }

    @Override
    public RefereeResponse findById(String refereeId) {
        Referee referee = refereeRepository.findById(refereeId)
                .orElseThrow(() -> new RefereeException("id",
                        String.format(RefereeException.REFEREE_NOT_FOUND, refereeId)));
        return RefereeMapper.toResponse(referee);
    }

    @Override
    public List<RefereeResponse> findAll() {
        return refereeRepository.findAll().stream().map(RefereeMapper::toResponse).toList();
    }
}
