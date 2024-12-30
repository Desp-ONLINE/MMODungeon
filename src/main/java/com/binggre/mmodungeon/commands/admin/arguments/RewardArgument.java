package com.binggre.mmodungeon.commands.admin.arguments;

import com.binggre.binggreapi.command.CommandArgument;
import com.binggre.binggreapi.command.annotations.ArgumentOption;
import com.binggre.binggreapi.utils.NumberUtil;
import com.binggre.mmodungeon.MMODungeon;
import com.binggre.mmodungeon.gui.RewardSettingGUI;
import com.binggre.mmodungeon.objects.base.Dungeon;
import com.binggre.mmodungeon.repository.DungeonRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@ArgumentOption(
        arg = "보상설정",
        description = "/인던 보상설정 [ID]",
        length = 2,
        permission = "mmodungeon.admin.reward",
        permissionMessage = "§c권한이 없습니다.",
        onlyPlayer = true
)
public class RewardArgument implements CommandArgument {

    private final DungeonRepository dungeonRepository = MMODungeon.getPlugin().getDungeonRepository();

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        int id = NumberUtil.parseInt(args[1]);
        if (id == NumberUtil.PARSE_ERROR) {
            sender.sendMessage("숫자를 입력해 주세요.");
            return false;
        }

        Dungeon dungeon = dungeonRepository.get(id);
        if (dungeon == null) {
            sender.sendMessage("존재하지 않는 던전입니다.");
            return false;
        }
        RewardSettingGUI.open((Player) sender, dungeon);
        return true;
    }
}