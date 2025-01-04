package com.binggre.mmodungeon.objects;

import com.binggre.binggreapi.functions.Callback;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.config.MessageConfig;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.objects.enums.DungeonType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PlayerClearLog {

    private final int dungeonId;
    private int count;
    private DungeonType dungeonType;
    private LocalDateTime lastClearTime;

    public PlayerClearLog(int dungeonId, int count) {
        this.dungeonId = dungeonId;
        this.count = count;
    }

    public boolean isCooldown(Callback<String> message) {
        if (lastClearTime == null) {
            return false;
        }
        Dungeon dungeon = MMODungeon.getPlugin().getDungeonRepository().get(dungeonId);
        if (dungeon == null) {
            return false;
        }

        int replayDay = dungeon.getReplayDay();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextAvailableDate = lastClearTime.toLocalDate().atStartOfDay().plusDays(replayDay);

        if (now.isBefore(nextAvailableDate)) {
            String msg = MessageConfig.getInstance().getReplyCooldownSingle()
                    .replace("<time>", nextAvailableDate.getMonthValue() + "월 " + nextAvailableDate.getDayOfMonth() + "일 00시");
            message.accept(msg);
            return true;
        }
        return false;
    }

    public void increase() {
        count++;
        lastClearTime = LocalDateTime.now();
    }

    public void decrease() {
        count--;
    }
}