package com.binggre.mmodungeon.objects.raid;

import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Location;

public class RaidSpawner {

    @Getter
    private String mobId;
    @Getter
    private int amount;

    @SerializedName("location")
    private String serializedLocation;

    @Getter
    private transient Location location;

    public void init(Dungeon dungeon) {
        location = DungeonRoom.deserializeLocation(dungeon, serializedLocation);
    }
}
