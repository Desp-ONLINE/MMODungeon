package com.binggre.mmodungeon.repository;

import com.binggre.binggreapi.functions.Callback;
import com.binggre.binggreapi.utils.file.FileManager;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mongolibraryplugin.MongoLibraryPlugin;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PlayerRepository {

    public static final String COLLECTION_NAME = "Player";

    private final MongoCollection<Document> collection;
    private final ConcurrentMap<UUID, PlayerDungeon> playerDungeons = new ConcurrentHashMap<>();
    private final ReplaceOptions options = new ReplaceOptions().upsert(true);

    public PlayerRepository() {
        collection = MongoLibraryPlugin.getInst().getMongoClient()
                .getDatabase(MMODungeon.DATA_BASE_NAME)
                .getCollection(COLLECTION_NAME);
    }

    public PlayerDungeon getFromMemory(UUID uuid) {
        return playerDungeons.get(uuid);
    }

    public void saveFromMemory(UUID uuid, PlayerDungeon playerDungeon) {
        playerDungeons.put(uuid, playerDungeon);
    }

    public void removeFromMemory(UUID uuid) {
        playerDungeons.remove(uuid);
    }

    public PlayerDungeon init(PlayerDungeon playerDungeon, UUID uuid) {
        if (playerDungeon == null) {
            playerDungeon = new PlayerDungeon(uuid);
        }
        saveAsync(playerDungeon);
        return playerDungeon;
    }

    public PlayerDungeon find(UUID uuid) {
        Document document = new Document("uuid", uuid.toString());
        Document find = collection.find(document).first();
        if (find == null) {
            return null;
        }

        return FileManager.toObject(find.toJson(), PlayerDungeon.class);
    }

    public void findAsync(UUID uuid, Callback<PlayerDungeon> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(MMODungeon.getPlugin(), () -> {
            PlayerDungeon playerDungeon = find(uuid);
            callback.accept(playerDungeon);
        });
    }

    public void save(PlayerDungeon playerDungeon) {
        String json = FileManager.toJson(playerDungeon);
        Document document = Document.parse(json);

        Bson filter = Filters.eq("uuid", playerDungeon.getUUID().toString());
        collection.replaceOne(filter, document, options);
    }

    public void saveAsync(PlayerDungeon playerDungeon) {
        Bukkit.getScheduler().runTaskAsynchronously(MMODungeon.getPlugin(), () -> {
            save(playerDungeon);
        });
    }

    public void drop() {
        collection.drop();
    }

    public void init() {
        playerDungeons.clear();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            UUID uuid = onlinePlayer.getUniqueId();
            PlayerDungeon playerDungeon = find(uuid);
            playerDungeon = init(playerDungeon, uuid);
            saveFromMemory(uuid, playerDungeon);
        }
    }
}