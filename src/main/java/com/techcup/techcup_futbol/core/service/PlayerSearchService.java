package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.PositionEnum;

import java.util.List;

public interface PlayerSearchService {
    List<Player> search(PositionEnum position, Integer semester, Integer minAge,
                        Integer maxAge, String gender, String name, Integer numberID);
}
