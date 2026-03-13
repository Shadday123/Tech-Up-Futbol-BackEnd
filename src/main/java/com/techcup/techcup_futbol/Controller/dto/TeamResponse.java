package com.techcup.techcup_futbol.Controller.dto;
import lombok.Data;
import java.util.List;

@Data
public class TeamResponse {

    private String id;
    private String teamName;
    private String shieldUrl;
    private String uniformColors;
    private String captainName;
    private List<String> players;
}
