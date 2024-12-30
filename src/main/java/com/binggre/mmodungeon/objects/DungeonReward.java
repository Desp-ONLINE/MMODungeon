package com.binggre.mmodungeon.objects;

import com.binggre.binggreapi.utils.serializers.ItemStackSerializer;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DungeonReward {

    @Getter
    private double exp;
    @Getter
    private double gold;

    @SerializedName("items")
    private String serializedItemsStacks;

    @Getter
    private transient List<ItemStack> itemStacks;

    public void setItems(Inventory inventory) {
        itemStacks = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                itemStacks.add(item);
            }
        }
        serializedItemsStacks = ItemStackSerializer.serializeItems(itemStacks);
    }

    public void init() {
        if (serializedItemsStacks == null || serializedItemsStacks.isEmpty()) {
            itemStacks = new ArrayList<>();
        } else {
            itemStacks = ItemStackSerializer.deserializeItems(serializedItemsStacks);
        }
    }
}