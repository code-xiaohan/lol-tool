package org.lele.sdtahzl.domain;

import lombok.Data;

@Data
public class SummonerInfo {
    private long summonerId;
    private long accountId;
    private String displayName;
    private String internalName;
    private int profileIconId;
    private int summonerLevel;
    private int xpSinceLastLevel;
    private int xpUntilNextLevel;
    private int percentCompleteForNextLevel;
    private RerollPoints rerollPoints;
    private String puuid;
    private boolean nameChangeFlag;
    private boolean unnamed;
    private String privacy;
    private String gameName;
    private String tagLine;
}
