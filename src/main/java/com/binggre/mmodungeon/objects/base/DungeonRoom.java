package com.binggre.mmodungeon.objects.base;

import com.binggre.binggreapi.functions.Callback;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mmodungeon.repository.DungeonRepository;
import com.binggre.mmodungeon.repository.PlayerRepository;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

public interface DungeonRoom {

    Plugin plugin = MMODungeon.getPlugin();

    PlayerRepository playerRepository = MMODungeon.getPlugin().getPlayerRepository();
    DungeonRepository dungeonRepository = MMODungeon.getPlugin().getDungeonRepository();

    BukkitScheduler scheduler = Bukkit.getScheduler();
    BukkitAPIHelper mythicMobAPI = MMODungeon.getPlugin().getMythicMobAPI();

    void init(Dungeon dungeon);

    void initJoin();

    Dungeon getConnected();

    Inventory getRewardInventory();

    void reward();

    void stop(boolean force);

    boolean isActive();

    void join(List<PlayerDungeon> playerDungeon);

    int getLife();

    void decreaseLife();

    void quit(PlayerDungeon playerDungeon);

    void teleport(Player player);

    void cancelTasks();

    static Location deserializeLocation(Dungeon dungeon, String serializedLocation) {
        String[] split = serializedLocation
                .replace(" ", "")
                .split(",");

        String world = null;
        double x = -1, y = -1, z = -1;
        float yaw = 0, pitch = 0;

        for (String element : split) {
            if (element.startsWith("world:")) {
                world = element.replace("world:", "");
            } else if (element.startsWith("x:")) {
                x = Double.parseDouble(element.replace("x:", ""));
            } else if (element.startsWith("y:")) {
                y = Double.parseDouble(element.replace("y:", ""));
            } else if (element.startsWith("z:")) {
                z = Double.parseDouble(element.replace("z:", ""));
            } else if (element.startsWith("yaw:")) {
                yaw = Float.parseFloat(element.replace("yaw:", ""));
            } else if (element.startsWith("pitch:")) {
                pitch = Float.parseFloat(element.replace("pitch:", ""));
            }
        }
        if (world == null || x == -1 || y == -1 || z == -1) {
            throw new NullPointerException(dungeon.getId() + "-ID 던전의 좌표가 잘못되었습니다. " + serializedLocation);
        }
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    default void cleaning(Location pos1, Location pos2, Callback<Object> completed) {
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        new BukkitRunnable() {
            int repeatCount = 10;

            @Override
            public void run() {
                if (repeatCount <= 0) {
                    completed.accept(null);
                    this.cancel();
                    return;
                }

                pos1.getWorld().getEntities().stream()
                        .filter(entity -> {
                            Location loc = entity.getLocation();
                            return loc.getX() >= minX && loc.getX() <= maxX &&
                                    loc.getY() >= minY && loc.getY() <= maxY &&
                                    loc.getZ() >= minZ && loc.getZ() <= maxZ;
                        })
                        .forEach(entity -> {
                            if (!(entity instanceof Player)) {
                                entity.remove();
                            }
                        });

                repeatCount--;
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }
}