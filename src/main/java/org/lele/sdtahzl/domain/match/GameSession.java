package org.lele.sdtahzl.domain.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 映射 LCU: /lol-gameflow/v1/session
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameSession {

    /** 当前阶段（如 FindingMatch / ChampSelect / InProgress / EndOfGame 等） */
    private String phase;

    private GameData gameData;
    private GameClient gameClient;
    private MapData map;
    private GameDodge gameDodge;

    // -------------------- gameData --------------------
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GameData {
        private long gameId;
        private String gameName;

        @JsonProperty("isCustomGame")
        private boolean customGame;

        private String password;
        private Queue queue;

        /** 选角界面：每边 5 人，结构稳定，直接建模 */
        private List<ChampSelectPlayer> teamOne;
        private List<ChampSelectPlayer> teamTwo;

        /** 你发的样例里也在 gameData 下，包含自己与队友/对手的选择（英雄/召唤师技能/皮肤索引） */
        private List<PlayerChampionSelection> playerChampionSelections;

        private boolean spectatorsAllowed;
    }

    // -------------------- queue --------------------
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Queue {
        private int id;
        private int mapId;

        private String name;
        private String shortName;
        private String description;
        private String detailedDescription;

        private String type;         // e.g. RANKED_SOLO_5x5
        private String gameMode;     // e.g. CLASSIC
        private String assetMutator; // 可能为 "" 或 "SWIFTPLAY"
        private String category;     // e.g. PvP

        private GameTypeConfig gameTypeConfig;

        private int numPlayersPerTeam;
        private int minimumParticipantListSize;
        private int maximumParticipantListSize;
        private int minLevel;

        @JsonProperty("isRanked")
        private boolean ranked;

        @JsonProperty("areFreeChampionsAllowed")
        private boolean freeChampionsAllowed;

        @JsonProperty("isTeamBuilderManaged")
        private boolean teamBuilderManaged;

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

        @JsonProperty("isCustom")
        private boolean custom;

        @JsonProperty("isBotHonoringAllowed")
        private boolean botHonoringAllowed;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
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

        private String pickMode; // e.g. TeamBuilderDraftPickStrategy
        private String banMode;  // e.g. StandardBanStrategy
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QueueRewards {
        @JsonProperty("isIpEnabled")
        private boolean ipEnabled;

        @JsonProperty("isXpEnabled")
        private boolean xpEnabled;

        @JsonProperty("isChampionPointsEnabled")
        private boolean championPointsEnabled;

        private List<Integer> partySizeIpRewards;
    }

    // -------------------- gameClient --------------------
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GameClient {
        private String serverIp;
        private int serverPort;

        private String observerServerIp;
        private int observerServerPort;

        private boolean running;
        private boolean visible;
    }

    // -------------------- map --------------------
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MapData {
        private int id;
        private String name;
        private String mapStringId;

        private String gameMode;         // e.g. CLASSIC
        private String gameModeName;     // e.g. 召唤师峡谷
        private String gameModeShortName;// e.g. 召唤峡谷
        private String gameMutator;      // e.g. SWIFTPLAY 或 ""

        @JsonProperty("isRGM")
        private boolean RGM;

        private String description;
        private String platformId;
        private String platformName;

        /** 素材路径（键值基本都是字符串路径） */
        private Map<String, String> assets;

        /** 暂时不做强约束 */
        private Map<String, Object> categorizedContentBundles;

        /** 目前看到的键：suppressRunesMasteriesPerks 等 */
        private Map<String, Object> properties;

        private Map<String, PerPositionSummonerSpells> perPositionRequiredSummonerSpells;
        private Map<String, PerPositionSummonerSpells> perPositionDisallowedSummonerSpells;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PerPositionSummonerSpells {
        private List<Integer> spells;
    }

    // -------------------- gameDodge --------------------
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GameDodge {
        private String state; // e.g. Invalid
        private List<Integer> dodgeIds;
        private String phase; // e.g. None
    }

    // -------------------- champ select structures --------------------
    /**
     * 选角期间，玩家条目（出现在 gameData.teamOne / teamTwo）
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChampSelectPlayer {
        private int championId;
        private int lastSelectedSkinIndex; // 注意：与 playerChampionSelections 里的 selectedSkinIndex 含义接近
        private int profileIconId;

        private String puuid;

        /** 位置与角色，如 TOP / JUNGLE / BOTTOM / MIDDLE / UTILITY 等 */
        private String selectedPosition;
        private String selectedRole;

        /** 这个 ID 在国服样例中可能非常大，必须 long */
        private long summonerId;

        private String summonerInternalName;
        private String summonerName;

        private boolean teamOwner;
        private int teamParticipantId;
    }

    /**
     * 选角期间的英雄/皮肤/召唤师技能选择（出现在 gameData.playerChampionSelections）
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerChampionSelection {
        private int championId;
        private String puuid;
        private int selectedSkinIndex;
        private int spell1Id;
        private int spell2Id;
    }
}
