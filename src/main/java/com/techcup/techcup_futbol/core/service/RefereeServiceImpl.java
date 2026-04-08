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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RefereeServiceImpl implements RefereeService {

    private static final Logger log = LoggerFactory.getLogger(RefereeServiceImpl.class);

    private final RefereeRepository refereeRepository;
    private final MatchRepository matchRepository;

    public RefereeServiceImpl(RefereeRepository refereeRepository, MatchRepository matchRepository) {
        this.refereeRepository = refereeRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    @Transactional
    public Referee create(String fullname, String email) {
        log.info("Registrando árbitro: {}", fullname);

        if (refereeRepository.existsByEmail(email)) {
            throw new RefereeException("email",
                    String.format(RefereeException.EMAIL_ALREADY_REGISTERED, email));
        }

        RefereeEntity refereeEntity = new RefereeEntity();
        refereeEntity.setId(IdGenerator.generateId());
        refereeEntity.setFullname(fullname);
        refereeEntity.setEmail(email);

        RefereeEntity saved = refereeRepository.save(refereeEntity);
        log.info("Árbitro registrado ID: {}", saved.getId());
        return RefereePersistenceMapper.toDomain(saved);
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
                        String.format(RefereeException.MATCH_NOT_FOUND, matchId)));

        if (matchEntity.getReferee() != null) {
            throw new RefereeException("match", RefereeException.MATCH_ALREADY_HAS_REFEREE);
        }

        matchEntity.setReferee(refereeEntity);
        matchRepository.save(matchEntity);

        refereeRepository.save(refereeEntity); // ✅ Guarda cambios en assignedMatches

        log.info("Árbitro '{}' asignado al partido {}", refereeEntity.getFullname(), matchId);
        return RefereePersistenceMapper.toDomain(refereeEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Referee findById(String refereeId) {
        RefereeEntity entity = refereeRepository.findById(refereeId)
                .orElseThrow(() -> new RefereeException("id",
                        String.format(RefereeException.REFEREE_NOT_FOUND, refereeId)));
        return RefereePersistenceMapper.toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Referee> findAll() {
        return refereeRepository.findAll().stream()
                .map(RefereePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
