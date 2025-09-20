package org.lele.sdtahzl.domain.match;

import lombok.Data;

@Data
public class Player {
    private String puuid;
    private String platformId;
    private long accountId;
    private long summonerId;
    private String summonerName;
    private String gameName;
    private String tagLine;
    private String currentPlatformId;
    private long currentAccountId;
    private String matchHistoryUri;
    private int profileIcon;
}