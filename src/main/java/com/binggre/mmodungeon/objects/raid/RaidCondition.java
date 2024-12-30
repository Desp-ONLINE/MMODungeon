package com.binggre.mmodungeon.objects.raid;

import lombok.Getter;

@Getter
public class RaidCondition implements Cloneable {

    private String mobId;
    private int amount;

    public void increase() {
        amount++;
    }

    public void reset() {
        amount = 0;
    }

    @Override
    public RaidCondition clone() {
        try {
            return (RaidCondition) super.clone();
        } catch (CloneNotSupportedException ignored) {
        }
        return null;
    }
}