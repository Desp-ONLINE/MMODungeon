package com.binggre.mmodungeon.objects;

import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import lombok.Getter;
import lombok.Setter;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.party.AbstractParty;
import net.Indyuce.mmocore.party.provided.Party;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDungeon {

    private final UUID uuid;
    // K dungeonId,
    private final Map<Integer, PlayerClearLog> clearLogs;
    private SerializedLocation prevLocation;

    @Getter
    @Setter
    private transient DungeonRoom joinedRoom;

    public UUID getUUID() {
        return uuid;
    }

    public boolean isJoin() {
        return joinedRoom != null;
    }

    public PlayerDungeon(UUID uuid) {
        this.uuid = uuid;
        this.clearLogs = new HashMap<>();
    }

    public Player toPlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            throw new IllegalStateException("Player is not online");
        }
        return player;
    }

    public void setPrevLocation(Location location) {
        if (location == null) {
            prevLocation = null;
            return;
        }
        prevLocation = new SerializedLocation(location);
    }

    public void teleportPrevLocation() {
        Player player = toPlayer();
        if (prevLocation != null) {
            player.teleport(prevLocation.toLocation());
            prevLocation = null;
        }
    }

    public boolean isJoinParty() {
        PlayerData playerData = PlayerData.get(uuid);
        AbstractParty party = MMODungeon.getPlugin().getMmoCore().partyModule.getParty(playerData);
        return party != null;
    }

    public Party getParty() {
        PlayerData playerData = PlayerData.get(uuid);
        return (Party) MMODungeon.getPlugin().getMmoCore().partyModule.getParty(playerData);
    }

    public PlayerClearLog getClearLog(int dungeonId) {
        return clearLogs.computeIfAbsent(dungeonId, id -> new PlayerClearLog(dungeonId, 0));
    }
}