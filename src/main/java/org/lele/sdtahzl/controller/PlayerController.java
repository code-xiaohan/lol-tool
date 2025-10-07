package org.lele.sdtahzl.controller;

import lombok.extern.slf4j.Slf4j;
import org.lele.sdtahzl.constant.ResultCode;
import org.lele.sdtahzl.domain.Result;
import org.lele.sdtahzl.domain.match.Game;
import org.lele.sdtahzl.domain.vo.MatchHistoryVO;
import org.lele.sdtahzl.service.ChampionService;
import org.lele.sdtahzl.service.PlayerService;
import org.lele.sdtahzl.util.LcuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/player")
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @Autowired
    private ChampionService championService;

    /**
     * 连接客户端， 获取LCU client
     * @return ture or false 表示成功或者失败
     */
    @GetMapping("/connect")
    public Result<Boolean> connect() {
        boolean result = LcuUtil.init();
        return result ? Result.ok() : Result.fail();
    }


    /**
     * 获取玩家战绩
     * @param gameName 游戏名
     * @param tagLine 游戏后面的数字
     * @return 玩家战绩
     */
    @PostMapping("/history/player/match")
    public Result<MatchHistoryVO> historyGameRecord(@RequestParam String gameName, String tagLine) {
        if (gameName == null || tagLine == null) {
            return Result.fail(ResultCode.PARAM_ERROR);
        }
        MatchHistoryVO matchHistoryVO = playerService.historyGameRecord(gameName, tagLine);
        return matchHistoryVO != null ? Result.ok(matchHistoryVO) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 获取当前登录玩家战绩
     * @return 玩家战绩
     */
    @GetMapping("/history/currentPlayer/match")
    public Result<MatchHistoryVO> historyCurrentPlayer() {
        MatchHistoryVO matchHistoryVO = playerService.currentPlayerRecord();
        return matchHistoryVO != null ? Result.ok(matchHistoryVO) : Result.fail(ResultCode.FAIL);
    }

    /**
     * 通过gameId获取对局信息
     * @return 对局详细信息
     */
    @GetMapping("/gameDetail")
    public Result<Game> gameDetail(@RequestParam String gameId) {
        Game gameDetailRecord = playerService.gameDetailRecord(gameId);
        return gameDetailRecord != null ? Result.ok(gameDetailRecord) : Result.fail(ResultCode.FAIL);
    }
    @GetMapping("/test")
    public Result test() throws Exception {
        playerService.test();
        return null;
    }
}
