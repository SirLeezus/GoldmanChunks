package lee.code.chunks.listeners;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ChunkCheckerListener implements Listener {

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            ItemStack handItem = player.getInventory().getItemInMainHand();

            if (handItem.getType().equals(Material.STICK)) {

                if (plugin.getData().getPlayerClickDelay(uuid)) return;
                else plugin.getPU().addPlayerClickDelay(uuid);

                if (e.getClickedBlock() != null) {
                    Chunk chunk = e.getClickedBlock().getLocation().getChunk();
                    String chunkCord = plugin.getPU().formatChunkLocation(chunk);
                    String owner = plugin.getSqLite().getChunkOwner(chunkCord);

                    if (cache.isAdminChunk(chunkCord)) owner = plugin.getPU().format("&4&lAdmin");

                    player.sendMessage(Lang.COMMAND_INFO_HEADER.getString(null));
                    player.sendMessage("");
                    player.sendMessage(Lang.COMMAND_INFO_LINE_1.getString(new String[] { owner }));
                    player.sendMessage(Lang.COMMAND_INFO_LINE_2.getString(new String[] { chunkCord }));
                    player.sendMessage("");
                    player.sendMessage(Lang.COMMAND_INFO_FOOTER.getString(null));

                    plugin.getPU().renderChunkBorder(player, chunk, "info");
                }
            }
        }
    }
}
