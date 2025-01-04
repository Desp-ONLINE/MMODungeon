package com.binggre.mmodungeon.objects.enums;

import lombok.Getter;

@Getter
public enum DungeonType {

    RAID("레이드"),
    INSTANCE_DUNGEON("인스턴스 던전");

    private final String name;

    DungeonType(String name) {
        this.name = name;
    }

    public static DungeonType fromString(String s) {
        try {
            return valueOf(s.toUpperCase().replace(" ", "_"));
        } catch (Exception e) {
            return null;
        }
    }
}