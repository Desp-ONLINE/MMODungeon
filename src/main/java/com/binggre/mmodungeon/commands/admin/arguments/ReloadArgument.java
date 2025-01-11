package com.binggre.mmodungeon.commands.admin.arguments;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.config.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadArgument implements CommandArgument {

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        MessageConfig.getInstance().init();
        MMODungeon.getPlugin().getDungeonManager().stopAll();
        MMODungeon.getPlugin().getDungeonRepository().init();
        commandSender.sendMessage("리로드 완료");
        return true;
    }

    @Override
    public String getArg() {
        return "리로드";
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "- 전체를 리로드합니다. (모든 던전 중지)";
    }

    @Override
    public String getPermission() {
        return "mmodungeon.admin.reload";
    }

    @Override
    public String getPermissionMessage() {
        return "§c권한이 없습니다.";
    }

    @Override
    public boolean onlyPlayer() {
        return false;
    }
}
