package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.PositionEnum;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.persistence.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class PlayerSearchServiceImpl implements PlayerSearchService {

    private static final Logger log = LoggerFactory.getLogger(PlayerSearchServiceImpl.class);

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public List<Player> search(PositionEnum position, Integer semester, Integer minAge,
                               Integer maxAge, String gender, String name, Integer numberID) {
        log.info("Buscando jugadores con filtros: position={} semester={} age={}-{} gender={} name={} id={}",
                position, semester, minAge, maxAge, gender, name, numberID);

        Stream<Player> stream = playerRepository.findAll().stream()
                .filter(p -> !p.isHaveTeam() && p.isDisponible());

        if (position != null) {
            stream = stream.filter(p -> position.equals(p.getPosition()));
        }
        if (gender != null && !gender.isBlank()) {
            stream = stream.filter(p -> gender.equalsIgnoreCase(p.getGender()));
        }
        if (minAge != null) {
            stream = stream.filter(p -> p.getAge() >= minAge);
        }
        if (maxAge != null) {
            stream = stream.filter(p -> p.getAge() <= maxAge);
        }
        if (name != null && !name.isBlank()) {
            String lowerName = name.toLowerCase();
            stream = stream.filter(p -> p.getFullname().toLowerCase().contains(lowerName));
        }
        if (numberID != null) {
            stream = stream.filter(p -> p.getNumberID() == numberID);
        }
        if (semester != null) {
            stream = stream.filter(p -> p instanceof StudentPlayer s
                    && s.getSemester() == semester);
        }

        List<Player> results = stream.toList();
        log.info("Jugadores encontrados: {}", results.size());
        return results;
    }
}
