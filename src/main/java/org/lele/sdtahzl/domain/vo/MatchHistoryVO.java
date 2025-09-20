package org.lele.sdtahzl.domain.vo;

import lombok.Data;
import org.lele.sdtahzl.domain.match.Games;

@Data
public class MatchHistoryVO {
    private String platformId;
    private long accountId;
    private Games games;
}