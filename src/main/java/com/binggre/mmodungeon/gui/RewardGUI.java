package com.binggre.mmodungeon.gui;

import com.binggre.binggreapi.functions.HolderListener;
import com.binggre.mmodungeon.objects.DungeonReward;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RewardGUI implements InventoryHolder, HolderListener {

    private final Inventory inventory;

    public RewardGUI(DungeonReward reward) {
        inventory = Bukkit.createInventory(this, 6 * 9, Component.text(""));
        int size = inventory.getSize();
        List<ItemStack> itemStacks = reward.getItemStacks();

        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            slots.add(i);
        }

        Collections.shuffle(slots);

        for (int i = 0; i < itemStacks.size(); i++) {
            if (i >= slots.size()) {
                break;
            }
            inventory.setItem(slots.get(i), itemStacks.get(i));
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || !(clickedInventory.getHolder() instanceof RewardGUI gui)) {
            return;
        }
        event.setCancelled(true);
        if (clickedInventory.getType() == InventoryType.PLAYER) {
            return;
        }
        if (event.getCurrentItem() == null) {
            return;
        }
        event.getWhoClicked().getInventory().addItem(event.getCurrentItem());
        inventory.setItem(event.getSlot(), null);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public void onDrag(InventoryDragEvent event) {

    }
}
