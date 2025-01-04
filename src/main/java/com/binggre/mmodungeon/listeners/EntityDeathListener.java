package com.binggre.mmodungeon.listeners;

import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import com.binggre.mmodungeon.objects.raid.RaidRoom;
import com.binggre.mmodungeon.repository.PlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

public class EntityDeathListener implements Listener {

    private final Plugin plugin = MMODungeon.getPlugin();
    private final PlayerRepository playerRepository = MMODungeon.getPlugin().getPlayerRepository();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerDungeon playerDungeon = playerRepository.get(player.getUniqueId());
        if (playerDungeon == null || !playerDungeon.isJoin()) {
            return;
        }

        DungeonRoom joinedRoom = playerDungeon.getJoinedRoom();
        if (!joinedRoom.isActive()) {
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            player.spigot().respawn();
            joinedRoom.teleport(player);
            joinedRoom.decreaseLife();
        });
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        LivingEntity killer = entity.getKiller();
        if (!(killer instanceof Player player)) {
            System.out.println("return1");
            return;
        }
        PlayerDungeon playerDungeon = playerRepository.get(player.getUniqueId());
        if (playerDungeon == null || !playerDungeon.isJoin()) {
            System.out.println("return2");
            return;
        }

        DungeonRoom joinDungeon = playerDungeon.getJoinedRoom();

        if (joinDungeon instanceof RaidRoom raidRoom) {
            raidRoom.checkClear(entity);
        }
    }
}