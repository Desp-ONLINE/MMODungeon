package com.binggre.mmodungeon.objects.raid;

import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.config.MessageConfig;
import com.binggre.mmodungeon.objects.DungeonReward;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import com.binggre.mmodungeon.objects.enums.DungeonType;
import lombok.Getter;

import java.util.List;

@Getter
public class Raid implements Dungeon {

    private int id;
    private boolean enable;
    private DungeonType type;
    private String name;
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

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void enable() {
        enable = true;
        MMODungeon.getPlugin().getDungeonRepository().updateActive(this, true);
    }

    @Override
    public void disable() {
        enable = false;
        rooms.forEach(dungeonRoom -> {
            if (dungeonRoom.isActive()) {
                return;
            }
            ((RaidRoom) dungeonRoom).sendMessage(MessageConfig.getInstance().getDisable());
            dungeonRoom.stop(true);
        });
        MMODungeon.getPlugin().getDungeonRepository().updateActive(this, false);
    }
}