package org.lele.sdtahzl.domain.match;

import lombok.Data;

import java.util.List;

@Data
public class Game {
    private String endOfGameResult;
    private long gameId;
    private String platformId;
    private long gameCreation;
    private String gameCreationDate;
    private long gameDuration;
    private int queueId;
    private int mapId;
    private int seasonId;
    private String gameVersion;
    private String gameMode;
    private List<String> gameModeMutators;
    private String gameType;
    private List<Team> teams;
    private List<Participant> participants;
    private List<ParticipantIdentity> participantIdentities;
}