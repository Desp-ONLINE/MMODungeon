package com.binggre.mmodungeon.api;

import com.binggre.mmodungeon.objects.PlayerDungeon;
import lombok.Getter;
import net.Indyuce.mmocore.party.provided.Party;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public abstract class DungeonEvent extends Event {

    private final List<PlayerDungeon> playerDungeons;

    @Nullable
    private final Party party;

    public DungeonEvent(List<PlayerDungeon> playerDungeons, @Nullable Party party) {
        this.playerDungeons = playerDungeons;
        this.party = party;
    }
}
