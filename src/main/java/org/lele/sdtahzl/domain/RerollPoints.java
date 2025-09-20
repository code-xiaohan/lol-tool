package org.lele.sdtahzl.domain;

import lombok.Data;

@Data
public class RerollPoints {
    private int pointsToReroll;
    private int currentPoints;
    private int numberOfRolls;
    private int maxRolls;
    private int pointsCostToRoll;
}