package org.lele.sdtahzl.domain.match;

import lombok.Data;

import java.util.List;

@Data
public class Team {
    private int teamId;
    private String win;
    private boolean firstBlood;
    private boolean firstTower;
    private boolean firstInhibitor;
    private boolean firstBaron;
    private boolean firstDargon;
    private int towerKills;
    private int inhibitorKills;
    private int baronKills;
    private int dragonKills;
    private int vilemawKills;
    private int riftHeraldKills;
    private int hordeKills;
    private int dominionVictoryScore;
    private List<Ban> bans;
}