package com.techcup.techcup_futbol.Controller.mapper;

import com.techcup.techcup_futbol.Controller.dto.PlayerDTO;
import com.techcup.techcup_futbol.core.model.InstitutionalPlayer;
import com.techcup.techcup_futbol.core.model.Player;
import com.techcup.techcup_futbol.core.model.RelativePlayer;
import com.techcup.techcup_futbol.core.model.StudentPlayer;
import com.techcup.techcup_futbol.core.model.*;

public class PlayerMapper {

    public static Player toModel(PlayerDTO dto) {

        if (dto == null){
            return null;
        }

        Player player = switch (dto.getPlayerType().toUpperCase()) {
            case "STUDENT" -> {
                StudentPlayer s = new StudentPlayer();
                s.setSemester(dto.getSemester());
                yield s;
            }
            case "INSTITUTIONAL" -> new InstitutionalPlayer();
            case "RELATIVE"      -> new RelativePlayer();
            default -> throw new IllegalArgumentException(
                    "Tipo de jugador no válido: " + dto.getPlayerType()
            );
        };

        player.setFullname(dto.getFullname());
        player.setEmail(dto.getEmail());
        player.setNumberID(dto.getNumberID());
        player.setPhotoUrl(dto.getPhotoUrl());
        player.setAge(dto.getAge());
        player.setGender(dto.getGender());
        player.setCaptain(dto.isCaptain());
        player.setPosition(dto.getPosition());
        player.setDorsalNumber(dto.getDorsalNumber());

        return player;
    }

    public static PlayerDTO toDTO(Player player) {

        if (player == null){
            return null;
        }

        PlayerDTO dto = new PlayerDTO();

        dto.setId(player.getId());
        dto.setFullname(player.getFullname());
        dto.setEmail(player.getEmail());
        dto.setNumberID(player.getNumberID());
        dto.setPhotoUrl(player.getPhotoUrl());
        dto.setAge(player.getAge());
        dto.setGender(player.getGender());
        dto.setCaptain(player.isCaptain());
        dto.setPosition(player.getPosition());
        dto.setDorsalNumber(player.getDorsalNumber());
        dto.setHaveTeam(player.isHaveTeam());

        // determinar tipo y atributos específicos
        if (player instanceof StudentPlayer s) {
            dto.setPlayerType("STUDENT");
            dto.setSemester(s.getSemester());
        } else if (player instanceof InstitutionalPlayer) {
            dto.setPlayerType("INSTITUTIONAL");
        } else if (player instanceof RelativePlayer) {
            dto.setPlayerType("RELATIVE");
        }

        return dto;
    }
}