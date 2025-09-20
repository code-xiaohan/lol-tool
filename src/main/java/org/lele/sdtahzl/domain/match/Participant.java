package org.lele.sdtahzl.domain.match;

import lombok.Data;

@Data
public class Participant {
    private int participantId;
    private int teamId;
    private int championId;
    private int spell1Id;
    private int spell2Id;
    private String highestAchievedSeasonTier;
    private Stats stats;
    private Timeline timeline;
}