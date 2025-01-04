package com.binggre.mmodungeon.commands.admin.arguments;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.command.annotations.ArgumentOption;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mmodungeon.objects.base.DungeonRoom;
import com.binggre.mmodungeon.repository.PlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@ArgumentOption(
        arg = "중지",
        description = "/인던 중지 <닉네임>",
        length = 2,
        permission = "mmodungeon.admin.stop",
        permissionMessage = "§c권한이 없습니다."
)
public class StopArgument implements CommandArgument {

    private final PlayerRepository playerRepository = MMODungeon.getPlugin().getPlayerRepository();

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§c존재하지 않는 플레이어입니다.");
            return false;
        }
        PlayerDungeon playerDungeon = playerRepository.get(target.getUniqueId());
        DungeonRoom dungeonRoom = null;
        if (playerDungeon == null || (dungeonRoom = playerDungeon.getJoinedRoom()) == null) {
            sender.sendMessage("해당 플레이어는 진행중이지 않습니다.");
            return false;
        }

        dungeonRoom.stop(true);

        sender.sendMessage(target.getName() + "님의 던전을 강제 중지했습니다.");
        return true;
    }
}
