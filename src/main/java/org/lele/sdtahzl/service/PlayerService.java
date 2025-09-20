package org.lele.sdtahzl.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.lele.sdtahzl.domain.ClientDTO;
import org.lele.sdtahzl.domain.SummonerInfo;
import org.lele.sdtahzl.domain.vo.MatchHistoryVO;
import org.lele.sdtahzl.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PlayerService {
    @Autowired
    private ClientService clientService;

    private static final int begin = 0;
    private static final int end = 1000;

    public MatchHistoryVO historyGameRecord(String summonerName) {
        ClientDTO client = null;
        try {
            client = clientService.getClient();
        } catch (Exception e) {
            log.error("客户端初始化失败{}", e.getMessage());
            return null;
        }

        String puuid = null;
        String puuidUrl = client.getPreUrl() + "/lol-summoner/v1/summoners";
        String authorization = client.getAuthToken();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authorization);
        String body = String.format("{\"name\" : \"%s\"}", summonerName);
        try {
            String resp = HttpUtil.post(puuidUrl, body, headers);
            SummonerInfo summonerInfo = JSON.parseObject(resp, SummonerInfo.class);
            if (summonerInfo == null) {
                return null;
            }
            puuid = summonerInfo.getPuuid();
        } catch (Exception e) {
            log.error("http get exception{}", e.getMessage(), e);
            return null;
        }

        String matchUrl = client.getPreUrl() + "/lol-match-history/v1/products/lol/" + puuid + "/matches";
        try {
            String resp = HttpUtil.get(matchUrl, headers);
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
}
