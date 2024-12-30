package com.binggre.mmodungeon.objects.enums;

public enum DungeonType {

    RAID("레이드"),
    INSTANCE_DUNGEON("인스턴스 던전");

    private final String name;

    DungeonType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static DungeonType fromString(String s) {
        try {
            return valueOf(s.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}