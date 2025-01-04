package com.binggre.mmodungeon.config;

import com.binggre.binggreapi.utils.file.FileManager;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mongolibraryplugin.MongoLibraryPlugin;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import org.bson.Document;

import java.util.Set;

@Getter
public class MainConfig {

    public static String COLLECTION = "Config-Main";
    private static MainConfig instance = null;

    public static MainConfig getInstance() {
        if (instance == null) {
            instance = new MainConfig();
        }
        return instance;
    }

    private Set<String> allowCommands;

    public void init() {
        MongoCollection<Document> collection = MongoLibraryPlugin.getInst().getMongoClient()
                .getDatabase(MMODungeon.DATA_BASE_NAME)
                .getCollection(COLLECTION);

        Document configDocument = collection.find().first();
        if (configDocument == null) {
            configDocument = new Document();
            configDocument.append("allowCommands", Set.of("dungeon"));
            collection.insertOne(configDocument);
        }
        instance = FileManager.toObject(configDocument.toJson(), MainConfig.class);
    }
}