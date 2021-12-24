package lee.code.chunks.commands.chunk;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.Cache;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TabCompletion implements TabCompleter {

    private final List<String> subCommands = Arrays.asList("teleport", "trust", "trustall", "trusted", "untrust", "untrustall", "claim", "unclaim", "map", "info", "manage", "autoclaim", "list", "abandonallclaims", "admin", "maxclaims", "setprice", "buy", "fly");
    private final List<String> blank = new ArrayList<>();

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        if (sender instanceof Player player) {
            UUID uuid = player.getUniqueId();
            if (args.length == 1) {
                List<String> hasCommand = new ArrayList<>();
                for (String pluginCommand : subCommands) if (sender.hasPermission("chunk.command." + pluginCommand)) hasCommand.add(pluginCommand);
                return StringUtil.copyPartialMatches(args[0], hasCommand, new ArrayList<>());

            } else if (args[0].equals("trust")) {
                if (args.length == 2) return StringUtil.copyPartialMatches(args[1], plugin.getPU().getOnlinePlayers(), new ArrayList<>());
            } else if (args[0].equals("trustall")) {
                if (args.length == 2) return StringUtil.copyPartialMatches(args[1], plugin.getPU().getOnlinePlayers(), new ArrayList<>());
            } else if (args[0].equals("untrust")) {
                if (args.length == 2) {
                    Chunk chunk = player.getLocation().getChunk();
                    String chunkCord = plugin.getPU().formatChunkLocation(chunk);
                    if (cache.isChunkClaimed(chunkCord) && cache.isChunkOwner(chunkCord, uuid)) return StringUtil.copyPartialMatches(args[1], cache.getChunkTrustedNames(chunkCord), new ArrayList<>());
                }
            } else if (args[0].equals("untrustall")) {
                if (args.length == 2) return StringUtil.copyPartialMatches(args[1], cache.getGlobalTrustedNames(uuid), new ArrayList<>());
            } else if (args[0].equals("teleport")) {
                if (args.length == 2) return StringUtil.copyPartialMatches(args[1], cache.getChunkClaims(uuid), new ArrayList<>());
            } else if (args[0].equals("admin")) {
                if (args.length == 2) return StringUtil.copyPartialMatches(args[1], Arrays.asList("unclaim", "unclaimall", "bypass", "bonusclaims"), new ArrayList<>());
                else if (args.length == 3 && args[1].equals("bonusclaims")) return StringUtil.copyPartialMatches(args[2], Arrays.asList("add", "remove", "set"), new ArrayList<>());
                else if (args.length == 4 && args[1].equals("bonusclaims")) return StringUtil.copyPartialMatches(args[3], plugin.getPU().getOnlinePlayers(), new ArrayList<>());
                else if (args.length == 5 && args[1].equals("bonusclaims")) return StringUtil.copyPartialMatches(args[4], Collections.singletonList("<amount>") , new ArrayList<>());
            }
        }
        return blank;
    }
}