package org.lele.sdtahzl.domain.match;

import lombok.Data;

import java.util.List;

@Data
public class Games {
    private int gameIndexBegin;
    private int gameIndexEnd;
    private String gameBeginDate;
    private String gameEndDate;
    private int gameCount;
    private List<Game> games;
}