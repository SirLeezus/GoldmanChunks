package lee.code.mychunks.commands.chunk.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.database.SQLite;
import lee.code.mychunks.files.defaults.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Map extends SubCommand {

    @Override
    public String getName() {
        return "map";
    }

    @Override
    public String getDescription() {
        return "Display a map of chunks around you in chat.";
    }

    @Override
    public String getSyntax() {
        return "/chunk map";
    }

    @Override
    public String getPermission() {
        return "mychunks.command.map";
    }

    @Override
    public void perform(Player player, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();
        UUID uuid = player.getUniqueId();
        SQLite SQL = plugin.getSqLite();

        List<String> chunkMap = new ArrayList<>();
        List<String> chunkSquare = new ArrayList<>();

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);

        chunkMap.add(Lang.COMMAND_MAP_HEADER.getConfigValue(null));

        String world = chunk.getWorld().getName();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            int firstX = chunk.getX() - 9;
            int x = chunk.getX() - 9;
            int z = chunk.getZ() - 5;

            for (int l = 1; l <= 11; l++) {
                for (int w = 1; w <= 19; w++) {

                    String chunkSelected = world + "," + x + "," + z;

                    if ((chunkSelected).equals(chunkCord)) {
                        chunkSquare.add("&9■");
                    } else if (SQL.isChunkOwner(chunkSelected, uuid)) {
                        chunkSquare.add("&2■");
                    } else if (SQL.isChunkTrusted(chunkSelected, uuid)) {
                        chunkSquare.add("&a■");
                    } else if (SQL.isChunkClaimed(chunkSelected)) {
                        UUID owner = SQL.getChunkOwnerUUID(chunkSelected);
                        if (SQL.isGlobalTrusted(owner, uuid)) chunkSquare.add("&a■");
                        else chunkSquare.add("&c■");
                    } else if (SQL.isAdminChunk(chunkSelected)) {
                        chunkSquare.add("&4■");
                    } else {
                        chunkSquare.add("&7■");
                    }
                    x++;
                }
                x = firstX;
                z++;

                String output = String.join(" ", chunkSquare);
                chunkMap.add(output);
                chunkSquare.clear();
            }
            for (String selectedChunk : chunkMap) player.sendMessage(plugin.getUtility().format(selectedChunk));

            String line1 = plugin.getUtility().format(" &e\\ &b&lN &e/ ");
            String line2 = plugin.getUtility().format(" &b&lW &6&l• &b&lE");
            String line3 = plugin.getUtility().format(" &e/ &b&lS &e\\");
            player.sendMessage(Lang.COMMAND_MAP_KEY_HEADER.getConfigValue(null));
            player.sendMessage("");
            player.sendMessage(Lang.COMMAND_MAP_LINE_1.getConfigValue(new String[] { line1 }));
            player.sendMessage(Lang.COMMAND_MAP_LINE_2.getConfigValue(new String[] { line2 }));
            player.sendMessage(Lang.COMMAND_MAP_LINE_3.getConfigValue(new String[] { line3 }));
            player.sendMessage("");
            player.sendMessage(Lang.COMMAND_MAP_FOOTER.getConfigValue(null));

        });
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
