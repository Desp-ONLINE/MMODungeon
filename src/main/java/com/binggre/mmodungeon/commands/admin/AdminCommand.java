package com.binggre.mmodungeon.commands.admin;

import com.binggre.binggreapi.command.BetterCommand;
import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.commands.admin.arguments.*;
import com.binggre.mmodungeon.objects.enums.DungeonType;
import com.binggre.mmodungeon.repository.DungeonRepository;
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

    private final DungeonRepository repository = MMODungeon.getPlugin().getDungeonRepository();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return argsMap.keySet().stream().toList();
        }

        String arg = args[0];
        return switch (arg) {
            case "목록" -> Arrays.stream(DungeonType.values())
                    .map(Enum::name)
                    .toList();
            case "활성화", "비활성화", "보상설정" -> getDungeonIdList();
            case "시작" -> args.length == 2 ? super.getOnlinePlayerNames() : getDungeonIdList();
            case "중지" -> args.length == 2 ? super.getOnlinePlayerNames() : List.of();
            default -> super.getOnlinePlayerNames();
        };
    }

    private List<String> getDungeonIdList() {
        return repository.values().stream()
                .map(dungeon -> String.valueOf(dungeon.getId()))
                .toList();
    }
}