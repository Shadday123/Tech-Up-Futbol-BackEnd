package com.techcup.techcup_futbol.util;
import com.techcup.techcup_futbol.controller.PlayerDTO;
import com.techcup.techcup_futbol.model.Player;
import com.techcup.techcup_futbol.model.StudentPlayer;

public class PlayerMapper {

    public static Player toModel(PlayerDTO dto){

        StudentPlayer player = new StudentPlayer();

        player.setFullname(dto.getFullname());
        player.setEmail(dto.getEmail());
        player.setPhotoUrl(dto.getPhotoUrl());
        player.setAge(dto.getAge());
        player.setGender(dto.getGender());
        player.setCaptain(dto.isCaptain());
        player.setPosition(dto.getPosition());
        player.setDorsalNumber(dto.getDorsalNumber());

        return player;
    }

    public static PlayerDTO toDTO(Player player){

        PlayerDTO dto = new PlayerDTO();

        dto.setId(player.getId());
        dto.setFullname(player.getFullname());
        dto.setEmail(player.getEmail());
        dto.setPhotoUrl(player.getPhotoUrl());
        dto.setAge(player.getAge());
        dto.setGender(player.getGender());
        dto.setCaptain(player.isCaptain());
        dto.setPosition(player.getPosition());
        dto.setDorsalNumber(player.getDorsalNumber());
        dto.setHaveTeam(player.isHaveTeam());

        return dto;
    }

}