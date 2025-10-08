package org.lele.sdtahzl.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.lele.sdtahzl.domain.item.Item;
import org.lele.sdtahzl.domain.item.Spell;
import org.lele.sdtahzl.domain.match.Participant;
import org.lele.sdtahzl.domain.match.Stats;
import org.lele.sdtahzl.util.LcuUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChampionService {

    /**
     * 获取英雄头像
     */
    public byte[] getChampionAvatar(int id) {
        String url = "/lol-game-data/assets/v1/champion-icons/" + id + ".png";
        try {
            return LcuUtil.doGetByte(url, null);
        } catch (Exception e) {
            log.error("get champion avatar error", e);
        }
        return null;
    }

    /**
     * 获取装备信息
     */
    public void getItemList(Stats stats) {
        try {
            String url = "/lol-game-data/assets/v1/items.json";
            String result = LcuUtil.doGet(url, null);
            List<Item> items = JSON.parseArray(result, Item.class);
            int item0 = stats.getItem0();
            int item1 = stats.getItem1();
            int item2 = stats.getItem2();
            int item3 = stats.getItem3();
            int item4 = stats.getItem4();
            int item5 = stats.getItem5();
            int item6 = stats.getItem6();
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                if (item0 == item.getId()) {
                    String itemUrl = item.getIconPath();
                    stats.setItem0Picture(LcuUtil.doGetByte(itemUrl, null));
                } else if (item1 == item.getId()) {
                    String itemUrl = item.getIconPath();
                    stats.setItem1Picture(LcuUtil.doGetByte(itemUrl, null));
                } else if (item2 == item.getId()) {
                    String itemUrl = item.getIconPath();
                    stats.setItem2Picture(LcuUtil.doGetByte(itemUrl, null));
                } else if (item3 == item.getId()) {
                    String itemUrl = item.getIconPath();
                    stats.setItem3Picture(LcuUtil.doGetByte(itemUrl, null));
                }else if (item4 == item.getId()) {
                    String itemUrl = item.getIconPath();
                    stats.setItem4Picture(LcuUtil.doGetByte(itemUrl, null));
                }else if (item5 == item.getId()) {
                    String itemUrl = item.getIconPath();
                    stats.setItem5Picture(LcuUtil.doGetByte(itemUrl, null));
                }else if (item6 == item.getId()) {
                    String itemUrl = item.getIconPath();
                    stats.setItem6Picture(LcuUtil.doGetByte(itemUrl, null));
                }
            }

        } catch (Exception e) {
            log.error("get item list error", e);
        }
    }

    /**
     * 获取召唤师技能
     */
    public void getPlayerSpells(Participant participant) {
        try {
            long spell1Id = participant.getSpell1Id();
            long spell2Id = participant.getSpell2Id();
            String url = "/lol-game-data/assets/v1/summoner-spells.json";
            String result = LcuUtil.doGet(url, null);
            List<Spell> spells = JSON.parseArray(result, Spell.class);
            for (int i = 0; i < spells.size(); i++) {
                Spell spell = spells.get(i);
                if (spell1Id == spell.getId()) {
                    String spellUrl = spell.getIconPath();
                    byte[] bytes = LcuUtil.doGetByte(spellUrl, null);
                    participant.setSpell1Picture(bytes);
                } else if (spell2Id == spell.getId()) {
                    String spellUrl = spell.getIconPath();
                    byte[] bytes = LcuUtil.doGetByte(spellUrl, null);
                    participant.setSpell2Picture(bytes);
                }
            }
        } catch (Exception e) {
            log.error("get player spells error", e);
        }
    }

    /**
     *
     */



}
