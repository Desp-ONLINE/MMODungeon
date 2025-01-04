package com.binggre.mmodungeon.repository;

import com.binggre.binggreapi.utils.file.FileManager;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import com.binggre.mmodungeon.objects.enums.DungeonType;
import com.binggre.mmodungeon.objects.raid.Raid;
import com.binggre.mmodungeon.objects.raid.RaidRoom;
import com.binggre.mongolibraryplugin.MongoLibraryPlugin;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DungeonRepository {

    public static final String COLLECTION_NAME = "Dungeon";
    private final MongoCollection<Document> collection;

    private final Map<Integer, Dungeon> dungeons = new HashMap<>();

    public DungeonRepository() {
        collection = MongoLibraryPlugin.getInst().getMongoClient()
                .getDatabase(MMODungeon.DATA_BASE_NAME)
                .getCollection(COLLECTION_NAME);
    }

    public Dungeon get(int id) {
        return dungeons.get(id);
    }

    public List<Dungeon> getAll() {
        return dungeons.values().stream().toList();
    }

    public void updateReward(Dungeon dungeon) {
        String json = FileManager.toJson(dungeon.getReward());
        Document document = Document.parse(json);
        Bson filter = Filters.eq("id", dungeon.getId());
        Bson update = Updates.set("reward", document);
        collection.updateOne(filter, update);
    }

    public void updateActive(Dungeon dungeon, boolean enable) {
        Bson filter = Filters.eq("id", dungeon.getId());
        Bson update = Updates.set("enable", enable);
        collection.updateOne(filter, update);
    }

    public void init() {
        dungeons.clear();

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
                dungeons.put(dungeon.getId(), dungeon);
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