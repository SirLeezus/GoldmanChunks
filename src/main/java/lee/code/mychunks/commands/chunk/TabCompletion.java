package lee.code.mychunks.commands.chunk;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.database.SQLite;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TabCompletion implements TabCompleter {

    private final List<String> subCommands = Arrays.asList("trust", "trustall", "trusted", "untrust", "untrustall", "claim", "unclaim", "map", "info", "manage", "autoclaim", "list", "abandonallclaims", "admin", "maxclaims");
    private final List<String> blank = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();

        if (sender instanceof Player) {
            if (args.length == 1) {
                List<String> hasCommand = new ArrayList<>();
                for (String pluginCommand : subCommands) if (sender.hasPermission("mychunks.command." + pluginCommand)) hasCommand.add(pluginCommand);
                return StringUtil.copyPartialMatches(args[0], hasCommand, new ArrayList<>());

            } else if (args[0].equals("trust")) {
                if (args.length == 2) return StringUtil.copyPartialMatches(args[1], plugin.getUtility().getOnlinePlayers(), new ArrayList<>());
            } else if (args[0].equals("trustall")) {
                if (args.length == 2) return StringUtil.copyPartialMatches(args[1], plugin.getUtility().getOnlinePlayers(), new ArrayList<>());
            } else if (args[0].equals("untrust")) {
                if (args.length == 2) {
                    Player player = (Player) sender;
                    UUID uuid = player.getUniqueId();
                    SQLite SQL = plugin.getSqLite();
                    Chunk chunk = player.getLocation().getChunk();
                    String chunkCord = plugin.getUtility().formatChunk(chunk);
                    if (SQL.isChunkOwner(chunkCord, uuid)) return StringUtil.copyPartialMatches(args[1], SQL.getTrustedToChunk(chunkCord), new ArrayList<>());
                }
            } else if (args[0].equals("untrustall")) {
                if (args.length == 2) {
                    Player player = (Player) sender;
                    UUID uuid = player.getUniqueId();
                    SQLite SQL = plugin.getSqLite();
                    return StringUtil.copyPartialMatches(args[1], SQL.getGlobalTrustedPlayers(uuid), new ArrayList<>());
                }
            } else if (args[0].equals("admin")) {
                if (args.length == 2) return StringUtil.copyPartialMatches(args[1], Arrays.asList("unclaim", "unclaimall", "bypass", "bonusclaims"), new ArrayList<>());
                else if (args.length == 3 && args[1].equals("bonusclaims")) return StringUtil.copyPartialMatches(args[2], Arrays.asList("add", "remove", "set"), new ArrayList<>());
                else if (args.length == 4 && args[1].equals("bonusclaims")) return StringUtil.copyPartialMatches(args[3], plugin.getUtility().getOnlinePlayers(), new ArrayList<>());
                else if (args.length == 5 && args[1].equals("bonusclaims")) return StringUtil.copyPartialMatches(args[4], Collections.singletonList("<amount>") , new ArrayList<>());
            }
        }

        return blank;
    }
}