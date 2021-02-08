package lee.code.chunks.commands.adminchunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.SQLite;
import lee.code.chunks.lists.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Claim extends SubCommand {

    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getDescription() {
        return "Select a group of chunks to claim.";
    }

    @Override
    public String getSyntax() {
        return "/adminchunk claim";
    }

    @Override
    public String getPermission() {
        return "mychunks.admin.claim";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunk(chunk);

        if (!SQL.isChunkClaimed(chunkCord)) {

            Vector start = new Vector(chunk.getX(), 0, chunk.getZ());
            String world = player.getWorld().getName();
            String selectedChunk = world + ",%.0f,%.0f";
            plugin.getPU().renderChunkBorder(player, chunk, "claim");

            if (!plugin.getData().hasAdminClaimSelection(player.getUniqueId())) {
                plugin.getData().addAdminClaimSelection(player.getUniqueId(), start);
                player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_ADMIN_CLAIM_FIRST_CHUNK_SELECTED.getString(new String[] { String.format(selectedChunk, start.getX(), start.getZ()) }));
                return;
            }

            Vector stop = plugin.getData().getAdminClaimSelection(player.getUniqueId());
            Vector max = Vector.getMaximum(start, stop);
            Vector min = Vector.getMinimum(start, stop);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                int claimed = 0;
                for (double x = min.getX(); x <= max.getX(); x++) {
                    for(double z = min.getZ(); z <= max.getZ(); z++) {
                        String inSelectionMessage = world + ",%.0f,%.0f";
                        String chunkCordSelected = String.format(inSelectionMessage, x, z);
                        if (!SQL.isChunkClaimed(chunkCordSelected) && !SQL.isAdminChunk(chunkCordSelected)) {
                            claimed++;
                            SQL.claimAdminChunk(chunkCordSelected);
                        }
                    }
                }
                plugin.getData().removeAdminClaimSelection(player.getUniqueId());
                player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_ADMIN_CLAIM_SUCCESSFUL.getString(new String[] { String.valueOf(claimed) } ));
            });
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
