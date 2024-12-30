package com.binggre.mmodungeon.commands.admin.arguments;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.command.annotations.ArgumentOption;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.config.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@ArgumentOption(
        arg = "리로드",
        description = "/인던 리로드 - 전체를 리로드합니다. (모든 던전 중지)",
        permission = "mmodungeon.admin.reload",
        permissionMessage = "§c권한이 없습니다."
)
public class ReloadArgument implements CommandArgument {

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        MessageConfig.getInstance().init();
        MMODungeon.getPlugin().getDungeonManager().stopAll();
        MMODungeon.getPlugin().getDungeonRepository().init();
        commandSender.sendMessage("리로드 완료");
        return true;
    }
}
