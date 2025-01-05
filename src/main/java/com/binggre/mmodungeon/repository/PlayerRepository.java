package com.binggre.mmodungeon.repository;

import com.binggre.binggreapi.utils.file.FileManager;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mongolibraryplugin.base.MongoCachedRepository;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;

public class PlayerRepository extends MongoCachedRepository<UUID, PlayerDungeon> {

    public PlayerRepository(Plugin plugin, String database, String collection, Map<UUID, PlayerDungeon> cache) {
        super(plugin, database, collection, cache);
    }

    @Override
    public Document toDocument(PlayerDungeon playerDungeon) {
        String json = FileManager.toJson(playerDungeon);
        Document parse = Document.parse(json);
        parse.put(ID_FILED, playerDungeon.getId().toString());
        return parse;
    }

    @Override
    public PlayerDungeon toEntity(Document document) {
        return FileManager.toObject(document.toJson(), PlayerDungeon.class);
    }

    public PlayerDungeon init(PlayerDungeon playerDungeon, UUID uuid) {
        if (playerDungeon == null) {
            playerDungeon = new PlayerDungeon(uuid);
        }
        saveAsync(playerDungeon);
        return playerDungeon;
    }

    public void init() {
        cache.clear();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            UUID uuid = onlinePlayer.getUniqueId();
            PlayerDungeon playerDungeon = findById(uuid);
            playerDungeon = init(playerDungeon, uuid);
            putIn(playerDungeon);
        }
    }
}