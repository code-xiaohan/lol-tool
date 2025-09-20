package org.lele.sdtahzl.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.lele.sdtahzl.domain.PlayerAlias;
import org.lele.sdtahzl.domain.SummonerInfo;
import org.lele.sdtahzl.domain.vo.MatchHistoryVO;
import org.lele.sdtahzl.util.LcuUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PlayerService {

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
        String url = "/lol-summoner/v1/current-summoner";
        try {
            String s = lcuUtil.doGet(url, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
