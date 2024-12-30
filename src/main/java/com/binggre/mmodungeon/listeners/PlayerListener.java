package com.binggre.mmodungeon.listeners;

import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import com.binggre.mmodungeon.repository.PlayerRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final PlayerRepository playerRepository = MMODungeon.getPlugin().getPlayerRepository();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        playerRepository.findAsync(uuid, playerRaid -> {
            playerRaid = playerRepository.init(playerRaid, uuid);
            playerRepository.saveFromMemory(uuid, playerRaid);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        PlayerDungeon playerDungeon = playerRepository.getFromMemory(uuid);
        if (playerDungeon == null) {
            return;
        }
        playerDungeon.teleportPrevLocation();
        playerDungeon.setPrevLocation(null);

        if (playerDungeon.isJoin()) {
            DungeonRoom joinedRoom = playerDungeon.getJoinedRoom();
            joinedRoom.quit(playerDungeon);
        }

        playerRepository.saveAsync(playerDungeon);
        playerRepository.removeFromMemory(uuid);
    }
}