package com.binggre.mmodungeon.managers;

import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;

public interface DungeonManager {

    void start(PlayerDungeon playerDungeon, int raidId);
    void stop(PlayerDungeon playerDungeon, boolean reward);

    default void stopAll() {
        for (Dungeon dungeon : MMODungeon.getPlugin().getDungeonRepository().getAll()) {
            for (DungeonRoom room : dungeon.getRooms()) {
                if (room.isActive()) {
                    room.stop(true);
                }
            }
        }
    }
}