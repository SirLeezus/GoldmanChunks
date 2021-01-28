package lee.code.mychunks.commands.adminchunk.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.database.SQLite;
import lee.code.mychunks.files.defaults.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class UnClaim extends SubCommand {

    @Override
    public String getName() {
        return "unclaim";
    }

    @Override
    public String getDescription() {
        return "Unclaim a group of admin chunks.";
    }

    @Override
    public String getSyntax() {
        return "/adminchunk unclaim";
    }

    @Override
    public String getPermission() {
        return "mychunks.admin.unclaim";
    }

    @Override
    public void perform(Player player, String[] args) {

        MyChunks plugin = MyChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);

        if (!SQL.isChunkClaimed(chunkCord)) {

            Vector start = new Vector(chunk.getX(), 0, chunk.getZ());
            String world = player.getWorld().getName();
            String selectedChunk = world + ",%.0f,%.0f";
            plugin.getUtility().renderChunkBorder(player, chunk, "claim");

            if (!plugin.getData().hasAdminClaimSelection(player.getUniqueId())) {
                plugin.getData().addAdminClaimSelection(player.getUniqueId(), start);
                player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_ADMIN_UNCLAIM_FIRST_CHUNK_SELECTED.getConfigValue(new String[] { String.format(selectedChunk, start.getX(), start.getZ()) }));
                return;
            }

            Vector stop = plugin.getData().getAdminClaimSelection(player.getUniqueId());
            Vector max = Vector.getMaximum(start, stop);
            Vector min = Vector.getMinimum(start, stop);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                int unclaimed = 0;
                for (double x = min.getX(); x <= max.getX(); x++) {
                    for(double z = min.getZ(); z <= max.getZ(); z++) {
                        String inSelectionMessage = world + ",%.0f,%.0f";
                        String chunkCordSelected = String.format(inSelectionMessage, x, z);
                        if (SQL.isAdminChunk(chunkCordSelected)) {
                            unclaimed++;
                            SQL.unClaimAdminChunk(chunkCordSelected);
                        }
                    }
                }
                plugin.getData().removeAdminClaimSelection(player.getUniqueId());
                player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_ADMIN_UNCLAIM_SUCCESSFUL.getConfigValue(new String[] { String.valueOf(unclaimed) } ));
            });
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
