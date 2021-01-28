package lee.code.mychunks.commands.adminchunk;

import lee.code.mychunks.MyChunks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminTabCompletion implements TabCompleter {

    private final List<String> subCommands = Arrays.asList("claim", "unclaim", "manage", "bypass");
    private final List<String> blank = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();

        if (sender instanceof Player) {
            if (args.length == 1) {
                List<String> hasCommand = new ArrayList<>();
                for (String pluginCommand : subCommands)
                    if (sender.hasPermission("mychunks.admin." + pluginCommand)) hasCommand.add(pluginCommand);
                return StringUtil.copyPartialMatches(args[0], hasCommand, new ArrayList<>());
            }
        }
        return blank;
    }
}