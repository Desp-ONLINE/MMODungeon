package com.binggre.mmodungeon.gui;

import com.binggre.binggreapi.functions.HolderListener;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.objects.DungeonReward;
import com.binggre.mmodungeon.objects.base.Dungeon;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RewardSettingGUI implements InventoryHolder, HolderListener {

    public static void open(Player player, Dungeon dungeon) {
        RewardSettingGUI gui = new RewardSettingGUI(dungeon);
        player.openInventory(gui.inventory);
    }

    private final Inventory inventory;
    private final Dungeon dungeon;

    private RewardSettingGUI(Dungeon dungeon) {
        this.dungeon = dungeon;
        this.inventory = create();
    }

    private Inventory create() {
        Inventory inventory = Bukkit.createInventory(this, 6 * 9, Component.text(dungeon.getName() + " 보상 설정"));
        DungeonReward reward = dungeon.getReward();
        for (ItemStack itemStack : reward.getItemStacks()) {
            inventory.addItem(itemStack);
        }
        return inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof RewardSettingGUI gui)) {
            return;
        }
        dungeon.getReward().setItems(event.getInventory());
        MMODungeon.getPlugin().getDungeonRepository().updateReward(dungeon);
    }

    @Override
    public void onDrag(InventoryDragEvent inventoryDragEvent) {

    }
}
