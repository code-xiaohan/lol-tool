package org.lele.sdtahzl.domain.vo;

import lombok.Data;
import org.lele.sdtahzl.domain.match.Game;

import java.util.List;

@Data
public class CurrentMatchDetailVO {
    private List<Game> game;
    private byte[] championPicture;
    private byte[] spell1Picture;
    private byte[] spell2Picture;
    private String gameName;
    private String tagLine;
    private byte[] avatarPicture;
    private String position;
    private String puuid;
}
