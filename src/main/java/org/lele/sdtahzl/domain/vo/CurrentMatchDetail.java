package org.lele.sdtahzl.domain.vo;

import lombok.Data;
import org.lele.sdtahzl.domain.match.Game;

@Data
public class CurrentMatchDetail {
    private Game game;
    private String championPicture;
    private String spell1Picture;
    private String spell2Picture;
    private String gameName;
    private String tagLine;
    private String avatarPicture;
}
