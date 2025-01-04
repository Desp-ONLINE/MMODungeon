package com.binggre.mmodungeon.api;

import com.binggre.mmodungeon.objects.base.DungeonRoom;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class DungeonFailedEvent extends DungeonEvent {

    private static final HandlerList handlers = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final FailedType failedType;
    private final DungeonRoom dungeonRoom;

    public DungeonFailedEvent(DungeonRoom dungeonRoom, FailedType failedType) {
        super(dungeonRoom.getJoinedPlayerDungeons(), dungeonRoom.getParty());
        this.dungeonRoom = dungeonRoom;
        this.failedType = failedType;
    }

    public enum FailedType {

        QUIT, TIMEOUT, FORCE, DEATH

    }
}