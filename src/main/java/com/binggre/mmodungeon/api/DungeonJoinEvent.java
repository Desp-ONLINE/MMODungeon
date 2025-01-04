package com.binggre.mmodungeon.api;

import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mmodungeon.objects.base.Dungeon;
import lombok.Getter;
import lombok.Setter;
import net.Indyuce.mmocore.party.provided.Party;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DungeonJoinEvent extends DungeonEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean cancelled;
    @Getter
    private final Dungeon dungeon;
    @Setter
    @Getter
    private String cancelMessage;

    public DungeonJoinEvent(Dungeon dungeon, List<PlayerDungeon> playerDungeons, Party party) {
        super(playerDungeons, party);
        this.dungeon = dungeon;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
