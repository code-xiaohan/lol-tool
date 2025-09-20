package org.lele.sdtahzl.domain.match;

import lombok.Data;

@Data
public class Stats {
    // 太长，只列代表性字段，其余可自动生成
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
    // … 其他字段省略，可按 JSON 一次性补全
}