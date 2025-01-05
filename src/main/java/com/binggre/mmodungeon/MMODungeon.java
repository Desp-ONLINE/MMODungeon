package com.binggre.mmodungeon;

import com.binggre.binggreapi.BinggrePlugin;
import com.binggre.mmodungeon.commands.admin.AdminCommand;
import com.binggre.mmodungeon.config.MainConfig;
import com.binggre.mmodungeon.config.MessageConfig;
import com.binggre.mmodungeon.listeners.BlockListener;
import com.binggre.mmodungeon.listeners.EntityDeathListener;
import com.binggre.mmodungeon.listeners.PlayerCommandListener;
import com.binggre.mmodungeon.listeners.PlayerListener;
import com.binggre.mmodungeon.managers.DungeonManager;
import com.binggre.mmodungeon.managers.RaidManager;
import com.binggre.mmodungeon.repository.DungeonRepository;
import com.binggre.mmodungeon.repository.PlayerRepository;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import lombok.Getter;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.HashMap;

@Getter
public final class MMODungeon extends BinggrePlugin implements Listener {

    public static final String DATA_BASE_NAME = "MMO-Dungeon";

    @Getter
    private static MMODungeon plugin;

    private MMOCore mmoCore;
    private MMOCoreAPI mmoCoreAPI;
    private BukkitAPIHelper mythicMobAPI;

    private DungeonManager dungeonManager;

    private PlayerRepository playerRepository;
    private DungeonRepository dungeonRepository;

    @Override
    public void onEnable() {
        plugin = this;

        mmoCore = MMOCore.plugin;
        mmoCoreAPI = new MMOCoreAPI(this);
        mythicMobAPI = new BukkitAPIHelper();

        playerRepository = new PlayerRepository(this, DATA_BASE_NAME, "Player", new HashMap<>());
        dungeonRepository = new DungeonRepository(this, DATA_BASE_NAME, "Dungeon", new HashMap<>());
        dungeonManager = new RaidManager(playerRepository, dungeonRepository);

        saveResource("example.json", true);
        executeCommand(this, new AdminCommand());
        registerEvents(this,
                this,
                new PlayerListener(),
                new EntityDeathListener(),
                new BlockListener(),
                new PlayerCommandListener()
        );

        playerRepository.init();
        dungeonRepository.init();
        MessageConfig.getInstance().init();
        MainConfig.getInstance().init();
    }

    @EventHandler
    private void onDisable(PluginDisableEvent event) {
        if (event.getPlugin() != this) {
            return;
        }
        dungeonManager.stopAll();
    }

    @Override
    public void onDisable() {
        playerRepository.values().forEach(playerDungeon -> playerRepository.save(playerDungeon));
    }
}