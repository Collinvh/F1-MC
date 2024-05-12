package collinvht.f1mc.module.main.command.commands;

import collinvht.f1mc.util.commands.CommandUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DiscordCommand extends CommandUtil {
    @Override
    protected void initializeCommand(@NotNull CommandSender commandSender) {
        addPart("%", 0, "/discord", (unused, unused1, unused2, unused3) -> prefix + "discord.gg/EEsFSrMsCX");
    }
}
