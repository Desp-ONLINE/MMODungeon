package com.binggre.mmodungeon.commands.admin.arguments;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.command.annotations.ArgumentOption;
import com.binggre.binggreapi.utils.NumberUtil;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.config.MessageConfig;
import com.binggre.mmodungeon.managers.DungeonManager;
import com.binggre.mmodungeon.objects.PlayerDungeon;
import com.binggre.mmodungeon.repository.PlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@ArgumentOption(
        arg = "시작",
        description = "/인던 시작 <닉네임> <ID>",
        length = 3,
        permission = "mmodungeon.admin.list",
        permissionMessage = "§c권한이 없습니다."
)
public class StartArgument implements CommandArgument {

    private final PlayerRepository playerRepository = MMODungeon.getPlugin().getPlayerRepository();
    private final DungeonManager dungeonManager = MMODungeon.getPlugin().getDungeonManager();

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String nickname = args[1];
        int id = NumberUtil.parseInt(args[2]);
        if (id == NumberUtil.PARSE_ERROR) {
            commandSender.sendMessage("숫자를 입력해 주세요.");
            return false;
        }
        Player player = Bukkit.getPlayer(nickname);
        if (player == null) {
            return false;
        }
        PlayerDungeon playerDungeon = playerRepository.getFromMemory(player.getUniqueId());

        if (playerDungeon.isJoin()) {
            player.sendMessage(MessageConfig.getInstance().getAlreadyPlay());
            return false;
        }

        dungeonManager.start(playerDungeon, id);
        return true;
    }
}