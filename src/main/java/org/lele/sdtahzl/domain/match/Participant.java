package org.lele.sdtahzl.domain.match;

import lombok.Data;

@Data
public class Participant {
    private int participantId;
    private int teamId;
    private int championId;
    private long spell1Id;
    private long spell2Id;
    private byte[] spell1Picture;
    private byte[] spell2Picture;
    private String highestAchievedSeasonTier;
    private Stats stats;
    private Timeline timeline;
    private byte[] championPicture;
}