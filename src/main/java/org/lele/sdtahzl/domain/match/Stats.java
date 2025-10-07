package org.lele.sdtahzl.domain.match;

import lombok.Data;

@Data
public class Stats {
    private int participantId;
    private boolean win;
    private int item0;
    private int item1;
    private int item2;
    private int item3;
    private int item4;
    private int item5;
    private int item6;
    private int kills;
    private int deaths;
    private int assists;
    private int goldEarned;
    private int champLevel;
    private int visionScore;
    private byte[] item0Picture;
    private byte[] item1Picture;
    private byte[] item2Picture;
    private byte[] item3Picture;
    private byte[] item4Picture;
    private byte[] item5Picture;
    private byte[] item6Picture;
    private String totalDamageDealtToChampions;
}