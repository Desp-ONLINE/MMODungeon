package com.binggre.mmodungeon.api.events;

import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class DungeonQuitEvent extends DungeonEvent {

    private static final HandlerList handlers = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final Player player;
    private final PlayerDungeon callerDungeon;
    private final DungeonRoom dungeonRoom;

    public DungeonQuitEvent(Player player, PlayerDungeon callerDungeon, DungeonRoom dungeonRoom) {
        super(dungeonRoom.getJoinedPlayerDungeons(), dungeonRoom.getParty());
        this.player = player;
        this.callerDungeon = callerDungeon;
        this.dungeonRoom = dungeonRoom;
    }
}