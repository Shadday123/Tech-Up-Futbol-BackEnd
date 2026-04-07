package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Referee;
import com.techcup.techcup_futbol.core.exception.RefereeException;
import com.techcup.techcup_futbol.core.util.IdGenerator;
import com.techcup.techcup_futbol.persistence.entity.MatchEntity;
import com.techcup.techcup_futbol.persistence.entity.RefereeEntity;
import com.techcup.techcup_futbol.persistence.mapper.RefereePersistenceMapper;
import com.techcup.techcup_futbol.persistence.repository.MatchRepository;
import com.techcup.techcup_futbol.persistence.repository.RefereeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        refereeRepository.save(RefereePersistenceMapper.toEntity(referee));
        log.info("Árbitro registrado ID: {}", referee.getId());
        return referee;
    }

    @Override
    @Transactional
    public Referee assignToMatch(String matchId, String refereeId) {
        log.info("Asignando árbitro ID: {} al partido ID: {}", refereeId, matchId);

        RefereeEntity refereeEntity = refereeRepository.findById(refereeId)
                .orElseThrow(() -> new RefereeException("refereeId",
                        String.format(RefereeException.REFEREE_NOT_FOUND, refereeId)));

        MatchEntity matchEntity = matchRepository.findById(matchId)
                .orElseThrow(() -> new RefereeException("matchId",
                        String.format(RefereeException.REFEREE_NOT_FOUND, matchId)));

        if (matchEntity.getReferee() != null) {
            throw new RefereeException("match", RefereeException.MATCH_ALREADY_HAS_REFEREE);
        }

        matchEntity.setReferee(refereeEntity);
        matchRepository.save(matchEntity);

        if (refereeEntity.getAssignedMatches() == null) {
            refereeEntity.setAssignedMatches(new ArrayList<>());
        }
        refereeEntity.getAssignedMatches().add(matchEntity);

        log.info("Árbitro '{}' asignado al partido {}", refereeEntity.getFullname(), matchId);
        return RefereePersistenceMapper.toDomain(refereeEntity);
    }

    @Override
    public Referee findById(String refereeId) {
        return refereeRepository.findById(refereeId)
                .map(RefereePersistenceMapper::toDomain)
                .orElseThrow(() -> new RefereeException("id",
                        String.format(RefereeException.REFEREE_NOT_FOUND, refereeId)));
    }

    @Override
    public List<Referee> findAll() {
        return refereeRepository.findAll().stream()
                .map(RefereePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
