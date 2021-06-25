package lee.code.chunks.commands.adminchunk;

import lee.code.chunks.GoldmanChunks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AdminTabCompletion implements TabCompleter {

    private final List<String> subCommands = Arrays.asList("claim", "unclaim", "manage", "bypass");
    private final List<String> blank = new ArrayList<>();

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        if (sender instanceof Player) {
            if (args.length == 1) {
                List<String> hasCommand = new ArrayList<>();
                for (String pluginCommand : subCommands)
                    if (sender.hasPermission("chunk.command." + pluginCommand)) hasCommand.add(pluginCommand);
                return StringUtil.copyPartialMatches(args[0], hasCommand, new ArrayList<>());
            }
        }
        return blank;
    }
}