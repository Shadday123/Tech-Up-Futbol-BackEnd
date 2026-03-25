package com.techcup.techcup_futbol.core.model;

import lombok.Data;

@Data
public class MatchEvent {

    private String id;

    private String type;

    private int minute;

    private Player player;

    private Match match;
}
