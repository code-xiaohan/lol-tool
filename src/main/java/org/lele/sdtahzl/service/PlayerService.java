package org.lele.sdtahzl.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.lele.sdtahzl.domain.PlayerAlias;
import org.lele.sdtahzl.domain.SummonerInfo;
import org.lele.sdtahzl.domain.match.Game;
import org.lele.sdtahzl.domain.vo.MatchHistoryVO;
import org.lele.sdtahzl.util.LcuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlayerService {
    @Autowired
    private ChampionService championService;

    private static final int begin = 0;
    private static final int end = 1000;
    private final LcuUtil lcuUtil;

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
     * 根据puuid查询 gameName 和 tagLine
     */

    /**
     * 测试api
     */
    public void test() throws Exception {
        String url = "/lol-champions/v1/inventories/" + "3660171163485600" + "/champions" + "/238";
        String s = LcuUtil.doGet(url, null);
        System.out.println(s);
    }
}
