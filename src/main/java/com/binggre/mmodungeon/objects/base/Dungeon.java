package com.binggre.mmodungeon.objects.base;

import com.binggre.mmodungeon.objects.DungeonReward;
import com.binggre.mmodungeon.objects.enums.DungeonType;
import com.binggre.mongolibraryplugin.base.MongoData;

import java.util.List;

public interface Dungeon extends MongoData<Integer> {

    void init(List<DungeonRoom> rooms);

    String getName();

    DungeonType getType();

    int getReplayDay();

    boolean isOnlySingle();

    String getRewardBlock();

    DungeonReward getReward();

    int getLife();

    int getReqLevel();

    int getMinPlayer();

    int getMaxPlayer();

    int getTime();

    int getMaxJoin();

    List<DungeonRoom> getRooms();

    DungeonRoom findEmptyRoom();

    boolean isEnable();

    void enable();

    void disable();
}