package org.lele.sdtahzl.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.lele.sdtahzl.domain.match.Game;
import org.lele.sdtahzl.domain.match.GameSession;
import org.lele.sdtahzl.util.LcuUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GameService {

    /**
     * 获取当前对局状态信息
     *
     * @request null
     * @respinse
     */
    public GameSession getGameSession() {
        try {
            String url = "/lol-gameflow/v1/session";
            String result = LcuUtil.doGet(url, null);
            return JSON.parseObject(result, GameSession.class);
        } catch (Exception e) {
            log.error("getGameSession error", e);
        }
        return null;
    }

    /**
     * 通过puuid查询游戏信息
     */
    public List<Game> getGames(String puuid) {
        try {
            String url = "/lol-gameflow/v1/session/" + puuid + "/game";
            String result = LcuUtil.doGet(url, null);
            return JSON.parseArray(result, Game.class);
        } catch (Exception e) {
            log.error("getGames error", e);
        }
        return null;
    }
}
