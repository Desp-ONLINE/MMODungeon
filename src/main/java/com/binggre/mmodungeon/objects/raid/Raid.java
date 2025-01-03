package com.binggre.mmodungeon.objects.raid;

import com.binggre.mmodungeon.objects.DungeonReward;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import com.binggre.mmodungeon.objects.enums.DungeonType;
import lombok.Getter;

import java.util.List;

@Getter
public class Raid implements Dungeon {

    private int id;
    private DungeonType type;
    private String name;
    private boolean enable;
    private int replayDay;
    private boolean onlySingle;

    private String rewardBlock;
    private DungeonReward reward;

    private List<RaidSpawner> spawners;
    private List<RaidCondition> conditions;

    private int life;
    private int reqLevel;

    private int minPlayer;
    private int maxPlayer;

    private int time;

    @Getter
    private int rewardTime;
    private int maxJoin;

    private transient List<DungeonRoom> rooms;

    @Override
    public void init(List<DungeonRoom> rooms) {
        this.rooms = rooms;
        this.rewardBlock = rewardBlock.replace(" ", "_").toUpperCase();
        this.reward.init();
        this.spawners.forEach(raidSpawner -> raidSpawner.init(this));
        rooms.forEach(raidRoom -> raidRoom.init(this));
    }

    @Override
    public DungeonRoom findEmptyRoom() {
        DungeonRoom emptyRoom = null;
        for (DungeonRoom room : rooms) {
            if (room.isActive()) {
                continue;
            }
            emptyRoom = room;
            break;
        }
        return emptyRoom;
    }
}