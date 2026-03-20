package com.techcup.techcup_futbol.Controller.mapper;

import com.techcup.techcup_futbol.Controller.dto.PlayerDTO;
import com.techcup.techcup_futbol.Controller.dto.PlayerResponse;
import com.techcup.techcup_futbol.core.model.*;

public class PlayerMapper {

    private PlayerMapper() {}

    /**
     * Convierte un PlayerDTO en la subclase concreta de Player correspondiente.
     * FIX: null guard agregado (los tests esperan null ante dto nulo).
     */
    public static Player toModel(PlayerDTO dto) {
        if (dto == null) return null;

        Player player = switch (dto.getPlayerType().toUpperCase()) {
            case "STUDENT" -> {
                StudentPlayer s = new StudentPlayer();
                s.setSemester(dto.getSemester() != null ? dto.getSemester() : 0);
                yield s;
            }
            case "INSTITUTIONAL" -> new InstitutionalPlayer();
            case "EXTERNAL"      -> new ExternalPlayer();
            default -> throw new IllegalArgumentException(
                    "Tipo de jugador no válido: " + dto.getPlayerType());
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

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            player.setPasswordHash(dto.getPassword());
        }

        return player;
    }

    public static PlayerDTO toDTO(Player player) {
        if (player == null) return null;

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


        if (player instanceof StudentPlayer s) {
            dto.setPlayerType("STUDENT");
            dto.setSemester(s.getSemester());
        } else if (player instanceof InstitutionalPlayer) {
            dto.setPlayerType("INSTITUTIONAL");
        } else if (player instanceof ExternalPlayer) {
            dto.setPlayerType("INTERNAL");
        } else {
            dto.setPlayerType("INSTITUTIONAL");
        }

        return dto;
    }

    public static PlayerResponse mapToResponse(Player player) {
        if (player == null) return null;

        Integer semester = null;
        String relationship = null;
        if (player instanceof StudentPlayer s) semester = s.getSemester();

        return new PlayerResponse(
                player.getId(),
                player.getFullname(),
                player.getEmail(),
                player.getPosition(),
                player.getDorsalNumber(),
                player.getPhotoUrl(),
                player.isHaveTeam(),
                player.getAge(),
                player.getGender(),
                player.isCaptain(),
                semester,
                relationship
        );
    }
}