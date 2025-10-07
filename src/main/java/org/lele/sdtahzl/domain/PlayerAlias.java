package org.lele.sdtahzl.domain;

import lombok.Data;

@Data
public class PlayerAlias {
    Alias alias;
    private String puuid;

    class Alias {
        private String gameName;
        private String togLine;
    }
}
