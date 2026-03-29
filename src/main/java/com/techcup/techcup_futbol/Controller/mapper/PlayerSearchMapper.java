package com.techcup.techcup_futbol.Controller.mapper;

import com.techcup.techcup_futbol.Controller.dto.PlayerSearchResult;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.StudentPlayer;

public class PlayerSearchMapper {

    private PlayerSearchMapper() {}

    public static PlayerSearchResult toResult(Player p) {
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
