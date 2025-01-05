package com.binggre.mmodungeon.repository;

import com.binggre.binggreapi.utils.file.FileManager;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import com.binggre.mmodungeon.objects.enums.DungeonType;
import com.binggre.mmodungeon.objects.raid.Raid;
import com.binggre.mmodungeon.objects.raid.RaidRoom;
import com.binggre.mongolibraryplugin.base.MongoCachedRepository;
import org.bson.Document;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DungeonRepository extends MongoCachedRepository<Integer, Dungeon> {

    public DungeonRepository(Plugin plugin, String database, String collection, Map<Integer, Dungeon> cache) {
        super(plugin, database, collection, cache);
    }

    @Override
    public Document toDocument(Dungeon dungeon) {
        String json = FileManager.toJson(dungeon);
        return Document.parse(json);
    }

    @Override
    public Dungeon toEntity(Document document) {
        return FileManager.toObject(document.toJson(), Dungeon.class);
    }

    public void init() {
        cache.clear();

        collection.find().forEach(document -> {
            String type = document.getString("type");
            DungeonType dungeonType = DungeonType.fromString(type);

            if (dungeonType == null) {
                String id = document.getString("id");
                MMODungeon.getPlugin()
                        .getLogger()
                        .log(Level.WARNING, String.format("[ID-%s] 존재하지 않는 던전 타입 : %s", id, type));
                return;
            }

            Dungeon dungeon = null;
            List<DungeonRoom> rooms = new ArrayList<>();

            switch (dungeonType) {
                case RAID -> {
                    dungeon = FileManager.toObject(document.toJson(), Raid.class);
                    rooms.addAll(documentToDungeonRooms(document.get("rooms"), RaidRoom.class));
                }
            }

            if (dungeon != null) {
                dungeon.init(rooms);
                putIn(dungeon);
            }
        });
    }

    private <T extends DungeonRoom> List<DungeonRoom> documentToDungeonRooms(Object roomObjects, Class<T> roomClass) {
        if (roomObjects == null) {
            return Collections.emptyList();
        }

        String roomJson = FileManager.toJson(roomObjects);
        List<Object> roomDataList = FileManager.toObject(roomJson, List.class);
        return roomDataList.stream()
                .map(data -> FileManager.toObject(FileManager.toJson(data), roomClass))
                .collect(Collectors.toList());
    }
}