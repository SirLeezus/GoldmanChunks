package lee.code.chunks.commands.adminchunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

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
        Cache cache = plugin.getCache();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (!cache.isChunkClaimed(chunkCord)) {
            Vector start = new Vector(chunk.getX(), 0, chunk.getZ());
            String world = player.getWorld().getName();
            String selectedChunk = world + ",%.0f,%.0f";
            plugin.getPU().renderChunkBorder(player, chunk, "claim");

            if (!plugin.getData().hasAdminClaimSelection(player.getUniqueId())) {
                plugin.getData().addAdminClaimSelection(player.getUniqueId(), start);
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_CLAIM_FIRST_CHUNK_SELECTED.getComponent(new String[] { String.format(selectedChunk, start.getX(), start.getZ()) })));
                return;
            }

            Vector stop = plugin.getData().getAdminClaimSelection(player.getUniqueId());
            Vector max = Vector.getMaximum(start, stop);
            Vector min = Vector.getMinimum(start, stop);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                List<String> chunks = new ArrayList<>();
                int claimed = 0;
                for (double x = min.getX(); x <= max.getX(); x++) {
                    for(double z = min.getZ(); z <= max.getZ(); z++) {
                        String inSelectionMessage = world + ",%.0f,%.0f";
                        String chunkCordSelected = String.format(inSelectionMessage, x, z);
                        if (!cache.isChunkClaimed(chunkCordSelected) && !cache.isAdminChunk(chunkCordSelected)) {
                            claimed++;
                            chunks.add(chunkCordSelected);
                        }
                    }
                }
                cache.claimBulkAdminChunk(chunks);
                plugin.getData().removeAdminClaimSelection(player.getUniqueId());
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_CLAIM_SUCCESSFUL.getComponent(new String[] { String.valueOf(claimed) } )));
            });
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_ADMIN_CLAIM_PLAYER_CLAIMED.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
