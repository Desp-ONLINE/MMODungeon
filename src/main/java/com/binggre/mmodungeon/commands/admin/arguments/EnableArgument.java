package com.binggre.mmodungeon.commands.admin.arguments;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.command.annotations.ArgumentOption;
import com.binggre.binggreapi.utils.NumberUtil;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.repository.DungeonRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


@ArgumentOption(
        arg = "활성화",
        description = "/인던 활성화 <ID>",
        length = 2,
        permission = "mmodungeon.admin.enable",
        permissionMessage = "§c권한이 없습니다."
)
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
}
