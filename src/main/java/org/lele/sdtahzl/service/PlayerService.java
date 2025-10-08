package org.lele.sdtahzl.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.lele.sdtahzl.domain.PlayerAlias;
import org.lele.sdtahzl.domain.SummonerInfo;
import org.lele.sdtahzl.domain.match.Game;
import org.lele.sdtahzl.domain.match.GameSession;
import org.lele.sdtahzl.domain.match.PlayerInfo;
import org.lele.sdtahzl.domain.vo.CurrentMatchDetailVO;
import org.lele.sdtahzl.domain.vo.MatchHistoryVO;
import org.lele.sdtahzl.util.LcuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlayerService {
    @Autowired
    private ChampionService championService;

    private static final int begin = 0;
    private static final int end = 1000;
    private final LcuUtil lcuUtil;
    @Autowired
    private GameService gameService;

    public PlayerService(LcuUtil lcuUtil) {
        this.lcuUtil = lcuUtil;
    }

    public MatchHistoryVO historyGameRecord(String gameName, String tagLine) {

        String puuid = null;
        Map<String, String> params = new HashMap<>();
        params.put("gameName", gameName);
        params.put("tagLine", tagLine);
        String url = "/lol-summoner/v1/alias/lookup";
        try {
            String resp = LcuUtil.doGet(url, params);
            PlayerAlias playerAlias = JSON.parseObject(resp, PlayerAlias.class);
            if (playerAlias == null) {
                return null;
            }
            puuid = playerAlias.getPuuid();
        } catch (Exception e) {
            log.error("http get exception{}", e.getMessage(), e);
            return null;
        }
        if (StringUtils.isEmpty(puuid)) {
            log.error("http get puuid is null");
            return null;
        }

        url = "/lol-match-history/v1/products/lol/" + puuid + "/matches";
        try {
            Map<String, String> param = new HashMap<>();
            param.put("begIndex", "0");
            param.put("endIndex", "100");
            String resp = LcuUtil.doGet(url, null);
            MatchHistoryVO matchHistoryVO = JSON.parseObject(resp, MatchHistoryVO.class);
            if (matchHistoryVO == null) {
                return null;
            }
            return matchHistoryVO;
        } catch (Exception e) {
            log.error("http get exception{}", e.getMessage(), e);
            return null;
        }
    }

    public MatchHistoryVO currentPlayerRecord() {
        String url = "/lol-match-history/v1/products/lol/current-summoner/matches";
        try {
            String result = lcuUtil.doGet(url, null);
            MatchHistoryVO matchHistoryVO = JSON.parseObject(result, MatchHistoryVO.class);
            // 获取所有参与者的头像信息
            matchHistoryVO.getGames().getGames().stream()
                    .flatMap(game -> game.getParticipants().stream())
                    .map(participant -> {
                        // 获取英雄ID
                        int championId = participant.getChampionId();

                        // 构造头像URL
                        byte[] championAvatar = championService.getChampionAvatar(championId);
                        participant.setChampionPicture(championAvatar);
                        return participant;
                    })
                    .toList();
            return matchHistoryVO;
        } catch (Exception e) {
            log.error("lcuUtil error{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param gameId
     * @return 游戏详细信息
     */
    public Game gameDetailRecord(String gameId) {
        String url = "/lol-match-history/v1/games/" + gameId;
        try {
            String result = lcuUtil.doGet(url, null);
            Game game = JSON.parseObject(result, Game.class);
            game.getParticipants()
                    .stream()
                    .map(participant -> {
                                int champtionId = participant.getChampionId();
                                byte[] championAvatar = championService.getChampionAvatar(champtionId);
                                participant.setChampionPicture(championAvatar);
                                championService.getItemList(participant.getStats());
                                championService.getPlayerSpells(participant);
                                return participant;
                            }
                    )
                    .toList();

            return game;
        } catch (Exception e) {
            log.error("lcuUtil error{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 查询当前对局的详细信息
     */
    public List<CurrentMatchDetailVO> getCurrentMatchDetail() {
        List<CurrentMatchDetailVO> result = new ArrayList<>();

        GameSession gameSession = gameService.getGameSession();
        if (gameSession == null || gameSession.getGameData() == null) {
            return result;
        }

        GameSession.GameData gameData = gameSession.getGameData();
        List<GameSession.ChampSelectPlayer> teamOne = gameData.getTeamOne() != null ? gameData.getTeamOne() : Collections.emptyList();
        // ✅ 修正：teamTwo 不能取 teamOne
        List<GameSession.ChampSelectPlayer> teamTwo = gameData.getTeamTwo() != null ? gameData.getTeamTwo() : Collections.emptyList();

        // ✅ 用 Map<puuid, PlayerChampionSelection> O(1) 命中选人阶段的召唤师技能，避免双重循环
        Map<String, GameSession.PlayerChampionSelection> selByPuuid = new HashMap<>();
        if (gameData.getPlayerChampionSelections() != null) {
            for (GameSession.PlayerChampionSelection sel : gameData.getPlayerChampionSelections()) {
                if (sel != null && sel.getPuuid() != null && !selByPuuid.containsKey(sel.getPuuid())) {
                    selByPuuid.put(sel.getPuuid(), sel);
                }
            }
        }

        // 合并两队，按 puuid 去重，保持顺序
        LinkedHashMap<String, CurrentMatchDetailVO> voByPuuid = new LinkedHashMap<>();
        List<GameSession.ChampSelectPlayer> allPlayers = new ArrayList<>();
        allPlayers.addAll(teamOne);
        allPlayers.addAll(teamTwo);

        for (GameSession.ChampSelectPlayer p : allPlayers) {
            if (p == null || p.getPuuid() == null) continue;
            String puuid = p.getPuuid();

            CurrentMatchDetailVO vo = voByPuuid.get(puuid);
            if (vo == null) {
                vo = new CurrentMatchDetailVO();
                vo.setPuuid(puuid);                            // 你后续依赖 puuid，请确保 VO 有该字段
                vo.setPosition(p.getSelectedPosition());       // 位置信息（两队都写）

                // 玩家名与标签
                try {
                    PlayerInfo info = getPlayerInfo(puuid);
                    if (info != null) {
                        vo.setGameName(info.getGameName());
                        vo.setTagLine(info.getTagLine());
                        // 如需头像且有接口，可在此设置：
                        // vo.setAvatarPicture(championService.getProfileIcon(info.getProfileIconId()));
                    }
                } catch (Exception ignore) {}

                // 英雄头像
                try {
                    vo.setChampionPicture(championService.getChampionAvatar(p.getChampionId()));
                } catch (Exception ignore) {}

                // 召唤师技能图片（从选人映射中一次命中）
                GameSession.PlayerChampionSelection sel = selByPuuid.get(puuid);
                if (sel != null) {
                    try { vo.setSpell1Picture(championService.getChampionAvatar(sel.getSpell1Id())); } catch (Exception ignore) {}
                    try { vo.setSpell2Picture(championService.getChampionAvatar(sel.getSpell2Id())); } catch (Exception ignore) {}
                }

                // 最近 10 场（若服务端返回多于 10，可在此截断）
                try {
                    List<Game> games = gameService.getGames(puuid);
                    if (games != null) {
                        games.stream().map(Game::getParticipants)
                                .flatMap(Collection::stream)
                                .map(participant -> {
                                            int champtionId = participant.getChampionId();
                                            byte[] championAvatar = championService.getChampionAvatar(champtionId);
                                            participant.setChampionPicture(championAvatar);
                                            championService.getItemList(participant.getStats());
                                            championService.getPlayerSpells(participant);
                                            return participant;
                                        }
                                )
                                .toList();
                        vo.setGame(games.size() > 10 ? games.subList(0, 10) : games);
                    } else {
                        vo.setGame(Collections.emptyList());
                    }
                } catch (Exception e) {
                    vo.setGame(Collections.emptyList());
                }

                voByPuuid.put(puuid, vo);
            } else {
                // 如果先前没写入 position，这里补一次
                if (vo.getPosition() == null) {
                    vo.setPosition(p.getSelectedPosition());
                }
                // 不重复写入其它字段
            }
        }

        result.addAll(voByPuuid.values());
        return result;
    }



    /**
     * @return 玩家个人信息
     * @request puuid
     */
    public PlayerInfo getPlayerInfo(String puuid) {
        try {
            String url = "/lol-summoner/v1/summoners-by-puuid-cached/" + puuid;
            String result = LcuUtil.doGet(url, null);
            return JSON.parseObject(result, PlayerInfo.class);
        } catch (Exception e) {
            log.error("get player spells error", e);
        }
        return null;
    }


    /**
     * 测试api
     */
    public void test() throws Exception {
        String url = "/lol-champions/v1/inventories/" + "3660171163485600" + "/champions" + "/238";
        String s = LcuUtil.doGet(url, null);
        System.out.println(s);
    }
}
