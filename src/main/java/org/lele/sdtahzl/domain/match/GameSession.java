package org.lele.sdtahzl.domain.match;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 对应 /lol-gameflow/v1/session 返回的结构
 */
@Data
public class GameSession {

    private String phase;
    private GameData gameData;
    private GameClient gameClient;
    private MapData map;
    private GameDodge gameDodge;

    @Data
    public static class GameData {
        private long gameId;
        private Queue queue;
        private boolean isCustomGame;
        private String gameName;
        private String password;
        private List<Map<String, Object>> teamOne;
        private List<Map<String, Object>> teamTwo;
        private List<Map<String, Object>> playerChampionSelections;
        private boolean spectatorsAllowed;
    }

    @Data
    public static class Queue {
        private int id;
        private int mapId;
        private String name;
        private String shortName;
        private String description;
        private String detailedDescription;
        private String type;
        private String gameMode;
        private String assetMutator;
        private String category;
        private GameTypeConfig gameTypeConfig;
        private int numPlayersPerTeam;
        private int minimumParticipantListSize;
        private int maximumParticipantListSize;
        private int minLevel;
        private boolean isRanked;
        private boolean areFreeChampionsAllowed;
        private boolean isTeamBuilderManaged;
        private String queueAvailability;
        private QueueRewards queueRewards;
        private boolean spectatorEnabled;
        private int championsRequiredToPlay;
        private List<Integer> allowablePremadeSizes;
        private boolean showPositionSelector;
        private long lastToggledOffTime;
        private long lastToggledOnTime;
        private boolean removalFromGameAllowed;
        private int removalFromGameDelayMinutes;
        private boolean isCustom;
        private boolean isBotHonoringAllowed;
    }

    @Data
    public static class GameTypeConfig {
        private int id;
        private String name;
        private int maxAllowableBans;
        private boolean allowTrades;
        private boolean exclusivePick;
        private boolean duplicatePick;
        private boolean teamChampionPool;
        private boolean crossTeamChampionPool;
        private boolean advancedLearningQuests;
        private boolean battleBoost;
        private boolean deathMatch;
        private boolean doNotRemove;
        private boolean learningQuests;
        private boolean onboardCoopBeginner;
        private boolean reroll;
        private int mainPickTimerDuration;
        private int postPickTimerDuration;
        private int banTimerDuration;
        private String pickMode;
        private String banMode;
    }

    @Data
    public static class QueueRewards {
        private boolean isIpEnabled;
        private boolean isXpEnabled;
        private boolean isChampionPointsEnabled;
        private List<Integer> partySizeIpRewards;
    }

    @Data
    public static class GameClient {
        private String serverIp;
        private int serverPort;
        private String observerServerIp;
        private int observerServerPort;
        private boolean running;
        private boolean visible;
    }

    @Data
    public static class MapData {
        private int id;
        private String name;
        private String mapStringId;
        private String gameMode;
        private String gameModeName;
        private String gameModeShortName;
        private String gameMutator;
        private boolean isRGM;
        private String description;
        private String platformId;
        private String platformName;
        private Map<String, Object> assets;
        private Map<String, Object> categorizedContentBundles;
        private Map<String, Object> properties;
        private Map<String, PerPositionSummonerSpells> perPositionRequiredSummonerSpells;
        private Map<String, PerPositionSummonerSpells> perPositionDisallowedSummonerSpells;
    }

    @Data
    public static class PerPositionSummonerSpells {
        private List<Integer> spells;
    }

    @Data
    public static class GameDodge {
        private String state;
        private List<Integer> dodgeIds;
        private String phase;
    }
}
