package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.PlayerSearchRequest;
import com.techcup.techcup_futbol.Controller.dto.PlayerSearchResult;
import com.techcup.techcup_futbol.core.model.DataStore;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class PlayerSearchServiceImpl implements PlayerSearchService {

    private static final Logger log = LoggerFactory.getLogger(PlayerSearchServiceImpl.class);

    @Override
    public List<PlayerSearchResult> search(PlayerSearchRequest f) {
        log.info("Buscando jugadores con filtros: {}", f);

        Stream<Player> stream = DataStore.jugadores.values().stream()
                .filter(p -> !p.isHaveTeam());

        if (f.position() != null) {
            stream = stream.filter(p -> f.position().equals(p.getPosition()));
        }
        if (f.gender() != null && !f.gender().isBlank()) {
            stream = stream.filter(p -> f.gender().equalsIgnoreCase(p.getGender()));
        }
        if (f.minAge() != null) {
            stream = stream.filter(p -> p.getAge() >= f.minAge());
        }
        if (f.maxAge() != null) {
            stream = stream.filter(p -> p.getAge() <= f.maxAge());
        }
        if (f.name() != null && !f.name().isBlank()) {
            String lowerName = f.name().toLowerCase();
            stream = stream.filter(p -> p.getFullname().toLowerCase().contains(lowerName));
        }
        if (f.numberID() != null) {
            stream = stream.filter(p -> p.getNumberID() == f.numberID());
        }
        if (f.semester() != null) {
            stream = stream.filter(p -> p instanceof StudentPlayer s
                    && s.getSemester() == f.semester());
        }

        List<PlayerSearchResult> results = stream.map(this::toResult).toList();
        log.info("Jugadores encontrados: {}", results.size());
        return results;
    }

    private PlayerSearchResult toResult(Player p) {
        String type = "INSTITUTIONAL";
        Integer semester = null;
        if (p instanceof StudentPlayer s) {
            type = "STUDENT";
            semester = s.getSemester();
        } else if (p.getClass().getSimpleName().equals("RelativePlayer")) {
            type = "RELATIVE";
        }
        return new PlayerSearchResult(
                p.getId(),
                p.getFullname(),
                p.getPosition(),
                p.getDorsalNumber(),
                p.getPhotoUrl(),
                type,
                semester,
                p.getAge(),
                p.getGender(),
                !p.isHaveTeam()
        );
    }
}
