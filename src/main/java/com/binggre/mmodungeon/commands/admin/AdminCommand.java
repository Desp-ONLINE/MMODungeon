package com.binggre.mmodungeon.commands.admin;

import com.binggre.binggreapi.command.BetterCommand;
import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.mmodungeon.commands.admin.arguments.*;
import com.binggre.mmodungeon.objects.enums.DungeonType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class AdminCommand extends BetterCommand implements TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return super.onCommand(commandSender, command, s, strings);
    }

    @Override
    public String getCommand() {
        return "인던";
    }

    @Override
    public boolean isSingleCommand() {
        return false;
    }

    @Override
    public List<CommandArgument> getArguments() {
        return List.of(
                new ListArgument(),
                new StartArgument(),
                new ReloadArgument(),
                new StopArgument(),
                new RewardArgument(),
                new EnableArgument(),
                new DisableArgument()
        );
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> list = argsMap.keySet().stream().toList();
        if (args.length == 1) {
            return list;
        }
        switch (args[0]) {
            case "목록" -> {
                return Arrays.stream(DungeonType.values())
                        .map(DungeonType::name)
                        .toList();
            }
            case "시작" -> {
                return null;
            }
        }
        return null;
    }
}