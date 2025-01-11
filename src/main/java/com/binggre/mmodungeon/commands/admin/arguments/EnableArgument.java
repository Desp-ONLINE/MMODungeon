package com.binggre.mmodungeon.commands.admin.arguments;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.utils.NumberUtil;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.repository.DungeonRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EnableArgument implements CommandArgument {

    private final DungeonRepository dungeonRepository = MMODungeon.getPlugin().getDungeonRepository();

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String arg = args[1];
        int id = NumberUtil.parseInt(arg);
        if (id == NumberUtil.PARSE_ERROR) {
            sender.sendMessage("아이디(숫자)를 입력해 주세요.");
            return false;
        }

        Dungeon dungeon = dungeonRepository.get(id);
        if (dungeon == null) {
            sender.sendMessage("존재하지 않는 아이디입니다.");
            return false;
        }
        dungeon.enable();
        sender.sendMessage(id + " 던전을 활성화했습니다.");
        return true;
    }

    @Override
    public String getArg() {
        return "활성화";
    }

    @Override
    public int length() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "[ID]";
    }

    @Override
    public String getPermission() {
        return "mmodungeon.admin.enable";
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
