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

    private final List<String> subCommands = Arrays.asList("claim", "unclaim", "manage", "bypass", "selection");
    private final List<String> blank = new ArrayList<>();

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();

        if (sender instanceof Player) {
            if (args.length == 1) {
                List<String> hasCommand = new ArrayList<>();
                for (String pluginCommand : subCommands)
                    if (sender.hasPermission("chunk.command." + pluginCommand)) hasCommand.add(pluginCommand);
                return StringUtil.copyPartialMatches(args[0], hasCommand, new ArrayList<>());
            } else if (args[0].equals("selection")) {
                if (args.length == 2) return StringUtil.copyPartialMatches(args[1], Arrays.asList("set", "clear", "claim", "unclaim"), new ArrayList<>());
                else if (args.length == 3) return StringUtil.copyPartialMatches(args[2], plugin.getData().getAdminChunkSettings(), new ArrayList<>());
                else if (args.length == 4) return StringUtil.copyPartialMatches(args[3], Arrays.asList("true", "false"), new ArrayList<>());
            }
        }
        return blank;
    }
}