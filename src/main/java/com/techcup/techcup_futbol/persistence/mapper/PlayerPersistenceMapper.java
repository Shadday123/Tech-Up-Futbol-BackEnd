package com.techcup.techcup_futbol.persistence.mapper;

import com.techcup.techcup_futbol.core.model.*;
import com.techcup.techcup_futbol.persistence.entity.*;

public class PlayerPersistenceMapper {

    private PlayerPersistenceMapper() {}

    public static PlayerEntity toEntity(Player player) {
        if (player == null) return null;

        PlayerEntity entity;

        if (player instanceof StudentPlayer s) {
            StudentPlayerEntity e = new StudentPlayerEntity();
            e.setSemester(s.getSemester());
            entity = e;
        } else if (player instanceof RelativePlayer r) {
            RelativePlayerEntity e = new RelativePlayerEntity();
            e.setParentship(r.getParentship());
            entity = e;
        } else if (player instanceof ExternalPlayer) {
            entity = new ExternalPlayerEntity();
        } else {
            // InstitutionalPlayer y cualquier otro
            entity = new InstitutionalPlayerEntity();
        }

        entity.setId(player.getId());
        entity.setFullname(player.getFullname());
        entity.setEmail(player.getEmail());
        entity.setPasswordHash(player.getPasswordHash());
        entity.setNumberID(player.getNumberID());
        entity.setPosition(player.getPosition());
        entity.setDorsalNumber(player.getDorsalNumber());
        entity.setPhotoUrl(player.getPhotoUrl());
        entity.setHaveTeam(player.isHaveTeam());
        entity.setDisponible(player.isDisponible());
        entity.setAge(player.getAge());
        entity.setGender(player.getGender());
        entity.setCaptain(player.isCaptain());
        entity.setSystemRole(player.getSystemRole());

        return entity;
    }


    public static Player toDomain(PlayerEntity entity) {
        if (entity == null) return null;

        Player player;

        if (entity instanceof StudentPlayerEntity s) {
            StudentPlayer p = new StudentPlayer();
            p.setSemester(s.getSemester());
            player = p;
        } else if (entity instanceof RelativePlayerEntity r) {
            RelativePlayer p = new RelativePlayer();
            p.setParentship(r.getParentship());
            player = p;
        } else if (entity instanceof ExternalPlayerEntity) {
            player = new ExternalPlayer();
        } else {
            player = new InstitutionalPlayer();
        }

        player.setId(entity.getId());
        player.setFullname(entity.getFullname());
        player.setEmail(entity.getEmail());
        player.setPasswordHash(entity.getPasswordHash());
        player.setNumberID(entity.getNumberID());
        player.setPosition(entity.getPosition());
        player.setDorsalNumber(entity.getDorsalNumber());
        player.setPhotoUrl(entity.getPhotoUrl());
        player.setHaveTeam(entity.isHaveTeam());
        player.setDisponible(entity.isDisponible());
        player.setAge(entity.getAge());
        player.setGender(entity.getGender());
        player.setCaptain(entity.isCaptain());
        player.setSystemRole(entity.getSystemRole());

        return player;
    }
}