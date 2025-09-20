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
    @GetMapping("/history/game/record{summonerName}")
    public Result<MatchHistoryVO> historyGameRecord(@PathVariable String summonerName) {
        if (summonerName == null) {
            return Result.fail(ResultCode.PARAM_ERROR);
        }
        MatchHistoryVO matchHistoryVO = playerService.historyGameRecord(summonerName);
        return matchHistoryVO != null ? Result.ok(matchHistoryVO)  : Result.fail(ResultCode.FAIL);
    }
}
