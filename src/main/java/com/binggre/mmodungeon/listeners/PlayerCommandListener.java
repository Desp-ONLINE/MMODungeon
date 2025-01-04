package com.binggre.mmodungeon.listeners;

import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.config.MainConfig;
import com.binggre.mmodungeon.config.MessageConfig;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mmodungeon.repository.PlayerRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Set;

public class PlayerCommandListener implements Listener {

    private final PlayerRepository repository = MMODungeon.getPlugin().getPlayerRepository();

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) {
            return;
        }
        String command = event.getMessage().toLowerCase();

        PlayerDungeon playerDungeon = repository.get(player.getUniqueId());
        if (playerDungeon == null || !playerDungeon.isJoin()) {
            return;
        }

        Set<String> allowCommands = MainConfig.getInstance().getAllowCommands();
        for (String allowCommand : allowCommands) {
            if (allowCommand.toLowerCase().startsWith(command)) {
                return;
            }
        }

        event.setCancelled(true);
        player.sendMessage(MessageConfig.getInstance().getAllowCommand());
    }
}