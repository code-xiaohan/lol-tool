package org.lele.sdtahzl.controller;

import lombok.extern.slf4j.Slf4j;
import org.lele.sdtahzl.constant.ResultCode;
import org.lele.sdtahzl.domain.Result;
import org.lele.sdtahzl.domain.vo.MatchHistoryVO;
import org.lele.sdtahzl.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/player")
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @PostMapping("/history/game/record")
    public Result<MatchHistoryVO> historyGameRecord(@RequestParam String gameName, String tagLine) {
        if (gameName == null || tagLine == null) {
            return Result.fail(ResultCode.PARAM_ERROR);
        }
        MatchHistoryVO matchHistoryVO = playerService.historyGameRecord(gameName, tagLine);
        return matchHistoryVO != null ? Result.ok(matchHistoryVO) : Result.fail(ResultCode.FAIL);
    }
    @GetMapping("/test")
    public Result test() {
        playerService.currentPlayerRecord();
        return null;
    }
}
