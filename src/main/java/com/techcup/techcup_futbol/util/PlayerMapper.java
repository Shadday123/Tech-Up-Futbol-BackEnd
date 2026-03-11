package com.techcup.techcup_futbol.util;
import com.techcup.techcup_futbol.model.Player;

public class PlayerMapper {

    public static Player toModel(PlayerDTO dto){
        Player player = new Player();
        player.setNombre(dto.getNombre());
        player.setCorreo(dto.getCorreo());
        player.setFoto(dto.getFoto());
        return player;
    }

    public static PlayerDTO toDTO(Player player){
        PlayerDTO dto = new PlayerDTO();
        dto.setNombre(player.getNombre());
        dto.setCorreo(player.getCorreo());
        dto.setFoto(player.getFoto());
        return dto;
    }
}