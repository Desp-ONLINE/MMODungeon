package com.binggre.mmodungeon.objects;

import com.binggre.binggreapi.utils.file.FileManager;
import com.binggre.binggreapi.utils.serializers.ItemStackSerializer;
import com.binggre.mongolibraryplugin.base.MongoObject;
import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DungeonReward implements MongoObject {

    private double exp;
    private double gold;

    @SerializedName("items")
    @Getter(AccessLevel.PRIVATE)
    private String serializedItemsStacks;

    private transient List<ItemStack> itemStacks;

    @Override
    public String toJson() {
        return FileManager.toJson(this);
    }

    public void setItems(Inventory inventory) {
        itemStacks = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                itemStacks.add(item);
            }
        }
        if (itemStacks.isEmpty()) {
            serializedItemsStacks = null;
            return;
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