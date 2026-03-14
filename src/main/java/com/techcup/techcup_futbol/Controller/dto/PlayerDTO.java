package com.techcup.techcup_futbol.Controller.dto;
import com.techcup.techcup_futbol.model.PositionEnum;
import lombok.Data;

@Data
public class PlayerDTO {

    private String id;

    private String fullname;

    private String email;

    private int numberID;

    private PositionEnum position;

    private int dorsalNumber;

    private String photoUrl;

    private boolean haveTeam;

    private int age;

    private String gender;

    private boolean captain;

}