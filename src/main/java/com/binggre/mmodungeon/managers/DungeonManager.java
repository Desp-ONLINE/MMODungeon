package com.binggre.mmodungeon.managers;

import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.api.DungeonJoinEvent;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import net.Indyuce.mmocore.party.provided.Party;
import org.bukkit.Bukkit;

import java.util.List;

public interface DungeonManager {

    void start(PlayerDungeon playerDungeon, int raidId);

    default void stopAll() {
        for (Dungeon dungeon : MMODungeon.getPlugin().getDungeonRepository().values()) {
            for (DungeonRoom room : dungeon.getRooms()) {
                if (room.isActive()) {
                    room.stop(true);
                }
            }
        }
    }

    default boolean callJoinEvent(Dungeon dungeon, List<PlayerDungeon> playerDungeons, Party party) {
        DungeonJoinEvent joinEvent = new DungeonJoinEvent(dungeon, playerDungeons, party);
        Bukkit.getPluginManager().callEvent(joinEvent);
        if (joinEvent.isCancelled()) {
            String cancelMessage = joinEvent.getCancelMessage();
            if (cancelMessage != null && !cancelMessage.isEmpty()) {
                playerDungeons.forEach(playerDungeon1 -> {
                    playerDungeon1.toPlayer().sendMessage(cancelMessage);
                });
            }
            return false;
        }
        return true;
    }
}