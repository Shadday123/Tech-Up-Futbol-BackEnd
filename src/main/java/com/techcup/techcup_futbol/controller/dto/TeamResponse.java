package com.techcup.techcup_futbol.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {

    private String id;
    private String teamName;
    private String shieldUrl;
    private List<String> uniformColors;
    private String captainName;
    private String captainId;
    private List<String> players;
}