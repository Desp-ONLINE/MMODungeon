package com.binggre.mmodungeon.api;

import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.repository.DungeonRepository;
import com.binggre.mmodungeon.repository.PlayerRepository;

public class MMODungeonAPI {

    public static PlayerRepository getPlayerRepository() {
        return MMODungeon.getPlugin().getPlayerRepository();
    }

    public static DungeonRepository getDungeonRepository() {
        return MMODungeon.getPlugin().getDungeonRepository();
    }
}