package com.binggre.mmodungeon.listeners;

import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import com.binggre.mmodungeon.repository.PlayerRepository;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener {

    private final PlayerRepository playerRepository = MMODungeon.getPlugin().getPlayerRepository();

    @EventHandler
    public void onClickBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerDungeon playerDungeon = playerRepository.getFromMemory(player.getUniqueId());
        if (playerDungeon == null || !playerDungeon.isJoin()) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        DungeonRoom joinedRoom = playerDungeon.getJoinedRoom();
        String rewardBlock = playerDungeon.getJoinedRoom().getConnected().getRewardBlock();
        if (!rewardBlock.equals(clickedBlock.getType().name())) {
            return;
        }
        event.setCancelled(true);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
        player.openInventory(joinedRoom.getRewardInventory());
    }
}