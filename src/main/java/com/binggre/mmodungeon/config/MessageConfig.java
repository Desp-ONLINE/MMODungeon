package com.binggre.mmodungeon.config;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.file.FileManager;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mongolibraryplugin.MongoLibraryPlugin;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.UUID;

@Getter
public class MessageConfig {

    public static String COLLECTION = "MessageConfig";
    private static MessageConfig instance = null;

    public static MessageConfig getInstance() {
        if (instance == null) {
            instance = new MessageConfig();
        }
        return instance;
    }

    private MessageConfig() {
    }

    private String prefix;
    private String timeActionbar;
    private String rewardActionbar;
    private String notFoundDungeon;
    private String failedLife;
    private String failedTime;
    private String numberOfPeople;
    private String alreadyPlay;
    private String replyCooldownSingle;
    private String replyCooldownParty;
    private String onlySingle;
    private String onlyPartyLeader;
    private String notFoundRoom;
    private String reqLevelSingle;
    private String reqLevelParty;
    private String allowCommand;
    private String disable;
    private String disableJoin;
    private String forceStop;

    public void init() {
        MongoCollection<Document> collection = MongoLibraryPlugin.getInst().getMongoClient()
                .getDatabase(MMODungeon.DATA_BASE_NAME)
                .getCollection(COLLECTION);

        Document configDocument = collection.find().first();

        if (configDocument == null) {
            configDocument = new Document();
            configDocument.append("prefix", "[던전] ")
                    .append("notFoundDungeon", "존재하지 않는 던전입니다.")
                    .append("failedLife", "생명을 모두 소진하여 소탕에 실패했습니다.")
                    .append("failedTime", "시간이 초과되어 소탕에 실패했습니다.")
                    .append("alreadyPlay", "이미 플레이중입니다.")
                    .append("numberOfPeople", "입장 가능한 인원 수는 <min>~<max>명 입니다.")
                    .append("onlySingle", "싱글 던전입니다. 파티원과 입장할 수 없습니다.")
                    .append("onlyPartyLeader", "파티장만 입장할 수 있습니다.")
                    .append("notFoundRoom", "존재하는 방이 없습니다.")
                    .append("replyCooldownSingle", "입장 횟수를 초과했습니다. <time> 이후에 입장할 수 있습니다.")
                    .append("replyCooldownParty", "§6<player>§f님께서 입장 횟수를 초과하여 입장할 수 없습니다.")
                    .append("reqLevelSingle", "레벨 <level> 이상만 입장할 수 있습니다.")
                    .append("reqLevelParty", "§6<player>§f님이 레벨이 충족되지 않아 입장할 수 없습니다.")
                    .append("timeActionbar", "남은 시간 : <time>")
                    .append("rewardActionbar", "퇴장 까지 시간 : <time>")
                    .append("allowCommand", "던전 진행중 사용할 수 없는 명령어입니다.")
                    .append("disable", "관리자가 던전을 비활성화했습니다. 강제 중단됩니다.")
                    .append("disableJoin", "비활성화 또는 점검중인 던전입니다. 잠시 후 다시 시도해 주세요.")
                    .append("forceStop", "관리자에 의해 던전이 중단되었습니다.");

            collection.insertOne(configDocument);
        }
        instance = FileManager.toObject(configDocument.toJson(), MessageConfig.class);
        instance.prefix = ColorManager.format(instance.prefix);

        for (Field declaredField : instance.getClass().getDeclaredFields()) {
            if (declaredField.getType() == String.class) {
                try {
                    String str = declaredField.get(instance).toString();
                    if (str.equals(instance.prefix) || str.equals(instance.timeActionbar) || str.equals(instance.rewardActionbar)) {
                        continue;
                    }
                    declaredField.setAccessible(true);
                    declaredField.set(instance, instance.prefix + ColorManager.format(str));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}