package com.binggre.mmodungeon.objects.raid;

import com.binggre.binggreapi.utils.EconomyManager;
import com.binggre.binggreapi.utils.NumberUtil;
import com.binggre.mmodungeon.api.DungeonClearEvent;
import com.binggre.mmodungeon.api.DungeonFailedEvent;
import com.binggre.mmodungeon.config.MessageConfig;
import com.binggre.mmodungeon.gui.RewardGUI;
import com.binggre.mmodungeon.objects.DungeonReward;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import com.google.gson.annotations.SerializedName;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.DespawnMode;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.party.provided.Party;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RaidRoom implements DungeonRoom {

    @SerializedName("pos1")
    private String serializedPos1;
    @SerializedName("pos2")
    private String serializedPos2;
    @SerializedName("playerWarpLocation")
    private String serializedPlayerWarpLocation;
    @SerializedName("rewardLocation")
    private String serializedRewardLocation;

    private transient Dungeon connectedDungeon;

    private transient Location pos1;
    private transient Location pos2;

    private transient Location playerWarpLocation;
    private transient Location rewardLocation;

    private transient boolean active = false;
    private transient boolean clearCondition = false;
    private transient RewardGUI rewardInventory;
    private transient Integer rewardTime;
    private transient Integer life;
    private transient Integer time;

    private transient List<Player> players;
    private transient Party party;
    private transient List<PlayerDungeon> joinedPlayerDungeons;
    private transient List<Integer> tasks;
    private transient List<RaidCondition> conditions;

    @Override
    public void init(Dungeon dungeon) {
        this.connectedDungeon = dungeon;

        pos1 = DungeonRoom.deserializeLocation(dungeon, serializedPos1);
        pos2 = DungeonRoom.deserializeLocation(dungeon, serializedPos2);

        playerWarpLocation = DungeonRoom.deserializeLocation(dungeon, serializedPlayerWarpLocation);
        rewardLocation = DungeonRoom.deserializeLocation(dungeon, serializedRewardLocation);
    }

    @Override
    public void initJoin() {
        conditions = new ArrayList<>();
        conditions = ((Raid) connectedDungeon).getConditions()
                .stream()
                .map(condition -> {
                    RaidCondition cloned = condition.clone();
                    cloned.reset();
                    return cloned;
                })
                .toList();

        rewardInventory = new RewardGUI(connectedDungeon.getReward());
        players = new ArrayList<>();
        joinedPlayerDungeons = new ArrayList<>();
        tasks = new ArrayList<>();
        rewardTime = ((Raid) connectedDungeon).getRewardTime();
        life = connectedDungeon.getLife();
        time = connectedDungeon.getTime();
    }

    @Override
    public Dungeon getConnected() {
        return connectedDungeon;
    }

    @Override
    public Inventory getRewardInventory() {
        return rewardInventory.getInventory();
    }

    @Override
    public void reward() {
        joinedPlayerDungeons.forEach(playerDungeon -> {
            Player player = playerDungeon.toPlayer();
            rewardExpGold(player, playerDungeon);
            player.teleport(rewardLocation);
        });
        startRewardTimeScheduler();
    }

    @Override
    public void stop(boolean force) {
        if (!force) {
            DungeonClearEvent clearEvent = new DungeonClearEvent(this);
            Bukkit.getPluginManager().callEvent(clearEvent);
        } else {
            DungeonFailedEvent failedEvent = new DungeonFailedEvent(this, DungeonFailedEvent.FailedType.FORCE);
            Bukkit.getPluginManager().callEvent(failedEvent);
        }

        cancelTasks();
        cleaning(pos1, pos2, o -> {
            active = false;
            clearCondition = false;
        });

        for (PlayerDungeon memberDungeon : joinedPlayerDungeons) {
            memberDungeon.teleportPrevLocation();
            memberDungeon.setJoinedRoom(null);
            memberDungeon.toPlayer().closeInventory();
            if (!force) {
                memberDungeon.getClearLog(connectedDungeon.getId())
                        .increase();
            }
            playerRepository.saveAsync(memberDungeon);
        }
        tasks = null;
        rewardTime = null;
        life = null;
        time = null;
        rewardInventory = null;
        players = null;
        joinedPlayerDungeons = null;
        conditions = null;
        party = null;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void join(List<PlayerDungeon> playerDungeons) {
        Raid raid = (Raid) connectedDungeon;
        List<RaidSpawner> spawners = raid.getSpawners();

        for (PlayerDungeon playerDungeon : playerDungeons) {
            Party party = playerDungeon.getParty();
            if (this.party == null && playerDungeon.getParty() != null) {
                this.party = party;
            }
            Player player = playerDungeon.toPlayer();
            players.add(player);
            playerDungeon.setJoinedRoom(this);
            playerDungeon.setPrevLocation(player.getLocation());
            player.teleport(playerWarpLocation);
            joinedPlayerDungeons.add(playerDungeon);
        }

        for (RaidSpawner spawner : spawners) {
            for (int i = 0; i < spawner.getAmount(); i++) {
                try {
                    Entity entity = mythicMobAPI.spawnMythicMob(spawner.getMobId(), spawner.getLocation());
                    ActiveMob mythicMobInstance = mythicMobAPI.getMythicMobInstance(entity);
                    mythicMobInstance.setDespawnMode(DespawnMode.NEVER);
                } catch (Exception ignored) {
                }
            }
        }

        this.active = true;
        startPlayTimeScheduler();
    }

    @Override
    public int getLife() {
        return life;
    }

    @Override
    public void decreaseLife() {
        life--;

        if (life < 0) {
            callFailedEvent(DungeonFailedEvent.FailedType.DEATH);
            sendMessage(MessageConfig.getInstance().getFailedLife());
            stop(false);
        }
    }

    @Override
    public void quit(PlayerDungeon playerDungeon) {
        players.remove(playerDungeon.toPlayer());
        joinedPlayerDungeons.remove(playerDungeon);
        playerDungeon.getClearLog(connectedDungeon.getId()).increase();

        if (players.isEmpty() || joinedPlayerDungeons.isEmpty()) {
            callFailedEvent(DungeonFailedEvent.FailedType.QUIT);
            stop(false);
        }
    }

    @Override
    public void teleport(Player player) {
        player.teleport(playerWarpLocation);
    }

    @Override
    public void cancelTasks() {
        tasks.forEach(scheduler::cancelTask);
    }

    @Override
    public List<PlayerDungeon> getJoinedPlayerDungeons() {
        return joinedPlayerDungeons;
    }

    @Nullable
    @Override
    public Party getParty() {
        return party;
    }

    private void rewardExpGold(Player player, PlayerDungeon playerDungeon) {
        UUID uuid = playerDungeon.getUUID();
        PlayerData playerData = PlayerData.get(uuid);

        DungeonReward reward = connectedDungeon.getReward();
        int division = joinedPlayerDungeons.size();

        double exp = reward.getExp() / division;
        double gold = reward.getGold() / division;

        playerData.giveExperience(exp, EXPSource.SOURCE);
        EconomyManager.addMoney(player, gold);
    }

    private void startPlayTimeScheduler() {
        int task = scheduler.runTaskTimer(plugin, () -> {
            if (clearCondition) {
                return;
            }
            if (time < 0) {
                callFailedEvent(DungeonFailedEvent.FailedType.TIMEOUT);
                sendMessage(MessageConfig.getInstance().getFailedTime());
                stop(false);
            } else {
                refreshActionbar(MessageConfig.getInstance().getTimeActionbar(), time);
                time--;
            }

        }, 20, 20).getTaskId();

        tasks.add(task);
    }

    private void startRewardTimeScheduler() {
        int task = scheduler.runTaskTimer(plugin, () -> {
            if (rewardTime < 0) {
                stop(false);

            } else {
                refreshActionbar(MessageConfig.getInstance().getRewardActionbar(), rewardTime);
                rewardTime--;
            }

        }, 20, 20).getTaskId();

        tasks.add(task);
    }

    private void refreshActionbar(String actionbar, int time) {
        TextComponent text = Component.text(actionbar.replace("<time>", NumberUtil.toTimeString(time)));

        for (Player player : players) {
            player.sendActionBar(text);
        }
    }

    public void checkClear(LivingEntity livingEntity) {
        if (clearCondition) {
            return;
        }
        ActiveMob mythicMobInstance = mythicMobAPI.getMythicMobInstance(livingEntity);
        if (mythicMobInstance == null) {
            return;
        }
        String internalName = mythicMobInstance.getType().getInternalName();
        Raid raid = (Raid) connectedDungeon;

        conditions.stream()
                .filter(condition -> condition.getMobId().equals(internalName))
                .forEach(RaidCondition::increase);

        int allCount = conditions.size();
        int clearCount = 0;
        for (int i = 0; i < allCount; i++) {
            int amount = conditions.get(i).getAmount();
            int originalAmount = raid.getConditions().get(i).getAmount();

            if (amount >= originalAmount) {
                clearCount++;
            }
        }

        if (clearCount >= allCount) {
            clearCondition = true;
            scheduler.runTaskLater(plugin, this::reward, 50L);
        }
    }

    public void sendMessage(String msg) {
        joinedPlayerDungeons.forEach(playerDungeon -> {
            playerDungeon.toPlayer().sendMessage(msg);
        });
    }
}