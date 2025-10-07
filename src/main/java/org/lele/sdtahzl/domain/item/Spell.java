package org.lele.sdtahzl.domain.item;

import lombok.Data;
import org.yaml.snakeyaml.events.Event;

import java.util.List;

@Data
//召唤师技能
public class Spell {
    private long id;
    private String name;
    private String description;
    private int summonerLevel;
    private int cooldown;
    private List<String> gameModes;
    private String iconPath;
}