package com.techcup.techcup_futbol.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor

public class MatchEvent {


    private String id;

    private String type;

    private int minute;

    private Player player;

    private Match match;
}
