package com.binggre.mmodungeon.commands.admin.arguments;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.objects.enums.DungeonType;
import com.binggre.mmodungeon.repository.DungeonRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListArgument implements CommandArgument {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        DungeonType type = DungeonType.fromString(args[1]);
        if (type == null) {
            sender.sendMessage("§c존재하지 않는 타입입니다.");
            return false;
        }
        DungeonRepository dungeonRepository = MMODungeon.getPlugin().getDungeonRepository();
        List<Dungeon> list = dungeonRepository.values()
                .stream()
                .filter(dungeon -> dungeon.getType() == type)
                .toList();

        if (list.isEmpty()) {
            sender.sendMessage("§c" + type.name() + " 타입의 던전이 존재하지 않습니다.");
            return false;
        }

        sender.sendMessage("[" + type.getName() + "]");
        int index = 1;
        for (Dungeon raid : dungeonRepository.values()) {
            String format = String.format("%s. ID<%s> %s", index, raid.getId(), raid.getName());
            sender.sendMessage(format);
            index++;
        }
        return true;
    }

    @Override
    public String getArg() {
        return "목록";
    }

    @Override
    public int length() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "- 던전 목록을 확인합니다.";
    }

    @Override
    public String getPermission() {
        return "mmodungeon.admin.list";
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
